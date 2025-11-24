import React, { useEffect, useState } from "react";
import { Card, Spin, message, Progress, Button, Space, Modal } from "antd";
import { useNavigate, useSearchParams } from "react-router-dom";
import chargingSessionAPI from "../../api/chargingSessionAPI";
import { endSessionManual } from "../../features/sessionSlice";
import { useDispatch } from "react-redux";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import transactionAPI from "../../api/transactionAPI";

function SessionInfo() {
  const [params] = useSearchParams();
  const sessionIdFromUrl = params.get("id");
  const [sessionId, setSessionId] = useState(sessionIdFromUrl || null);
  const [sessionData, setSessionData] = useState(null);
  const [loading, setLoading] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [paymentModalOpen, setPaymentModalOpen] = useState(false);
  const [paying, setPaying] = useState(false);

  // 🔁 Lấy từ localStorage nếu URL không có id (giữ nguyên)
  useEffect(() => {
    if (!sessionIdFromUrl) {
      const saved = localStorage.getItem("activeSession");
      if (saved) {
        const parsed = JSON.parse(saved);
        setSessionId(parsed.sessionId);
        setSessionData(parsed.sessionData);
      }
    }
  }, [sessionIdFromUrl]);

  // ⬇⬇ Fetch 1 lần ban đầu để có dữ liệu trước khi websocket gửi
  useEffect(() => {
    if (!sessionId) return;

    const fetchSession = async () => {
      try {
        setLoading(true);
        const res = await chargingSessionAPI.getByReservation(sessionId);
        if (res.data) {
          setSessionData(res.data);
          localStorage.setItem(
            "activeSession",
            JSON.stringify({
              sessionId,
              sessionData: res.data,
            })
          );
        }
      } catch (err) {
        console.error("Lỗi khi lấy thông tin sạc:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchSession();
  }, [sessionId]);

  // ✅ WebSocket / STOMP để nhận update realtime
  useEffect(() => {
    if (!sessionId) return;

    const socket = new SockJS("http://localhost:8080/ws");
    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 1000, // auto reconnect
    });

    stompClient.onConnect = () => {
      // subscribe topic mà backend đang gửi
      const topic = `/topic/session/${sessionId}`;
      stompClient.subscribe(topic, (message) => {
        const body = JSON.parse(message.body);
        setSessionData(body);

        // lưu localStorage
        localStorage.setItem(
          "activeSession",
          JSON.stringify({
            sessionId,
            sessionData: body,
          })
        );

        // nếu hoàn tất thì clear
        if (body.status === "COMPLETED" || body.status === "ENDED") {
          localStorage.removeItem("activeSession");
          message.success("🔋 Phiên sạc đã hoàn tất!");
        }
      });
    };

    stompClient.onStompError = (frame) => {
      console.error("STOMP error:", frame);
    };

    stompClient.activate();

    // cleanup khi unmount / đổi sessionId
    return () => {
      if (stompClient && stompClient.active) {
        stompClient.deactivate();
      }
    };
  }, [sessionId]);

  // 💡 Hiển thị progress SoC
  const soc = sessionData?.endSoc ?? sessionData?.startSoc ?? 0;

  // kWh tiêu thụ: dùng energyConsumed
  const energy =
    sessionData?.energyConsumed != null
      ? sessionData.energyConsumed.toFixed(2)
      : "--";

  // cost giữ nguyên, chỉ bảo vệ null một chút
  const cost =
    sessionData?.cost != null
      ? Math.floor(sessionData.cost).toLocaleString("vi-VN") + " đ"
      : "--";

  const status = sessionData?.status || "UNKNOWN";

  const handleEndSession = async () => {
    if (!sessionId) return;
    try {
      const endedSession = await dispatch(endSessionManual(sessionId)).unwrap();

      // Nếu backend trả session mới, lưu lại để lấy cost, transactionId, ...
      setSessionData(endedSession);

      message.success("⚡ Phiên sạc đã kết thúc!");
      localStorage.removeItem("activeSession");

      // 👉 Chỉ mở Modal khi gọi API thành công (200)
      setPaymentModalOpen(true);
    } catch (error) {
      console.error("End session error:", error);
      message.error("Không thể kết thúc phiên sạc!");
    }
  };
  const handlePayEWallet = async () => {
    if (!sessionId) {
      message.error("Không tìm thấy sessionId!");
      return;
    }

    try {
      setPaying(true);

      // 1. Lấy transaction theo sessionId
      const res = await transactionAPI.getTransactionFromSession(sessionId);
      const transaction = res.data;
      const transactionId = transaction.id;

      if (!transactionId) {
        message.error("Không tìm thấy transactionId cho phiên sạc này!");
        return;
      }

      // 2. Confirm thanh toán EWallet
      await transactionAPI.confirmPayment(transactionId);

      message.success("Thanh toán EWallet thành công!");
      setPaymentModalOpen(false);

      // 3. Chuyển sang trang lịch sử để thấy PENDING -> SUCCESS
      navigate("/user/history");
    } catch (err) {
      console.error("Thanh toán EWallet lỗi:", err);
      message.error("Thanh toán EWallet thất bại!");
    } finally {
      setPaying(false);
    }
  };
  return (
    <div className="w-full flex justify-center mt-10">
      <Card title="⚡ Seesion" className="w-[480px] shadow-lg text-center">
        {loading && (
          <div className="flex justify-center py-10">
            <Spin tip="Đang tải dữ liệu..." />
          </div>
        )}

        {!loading && sessionData && (
          <div className="flex flex-col items-center gap-4">
            <img
              src="/ev_logo.png"
              alt="charging"
              className="w-24 h-auto mb-3"
            />

            {/* Thanh tiến trình SoC */}
            <Progress
              type="circle"
              percent={soc}
              size={180}
              strokeColor={{
                "0%": "#22c55e",
                "100%": "#16a34a",
              }}
              format={(p) => `${p}%`}
            />

            <p className="text-lg font-semibold mt-5">
              <b> ⚙️ Status: </b>{" "}
              <span
                className={
                  status === "CHARGING"
                    ? "text-green-600"
                    : status === "COMPLETED"
                    ? "text-blue-600"
                    : "text-gray-600"
                }
              >
                {status}
              </span>
            </p>
            <p>
              🔋 <b>Energy:</b> {energy} kWh
            </p>
            <p>
              💰 <b>Cost:</b> {cost}
            </p>
            {/* ✅ Nút dừng sạc */}
            {status === "CHARGING" && (
              <Button
                type="primary"
                danger
                onClick={handleEndSession}
                className="mt-4 bg-red-500 hover:bg-red-600"
              >
                STOP
              </Button>
            )}
          </div>
        )}
        <Modal
          title="🔋 Phiên sạc đã hoàn thành"
          open={paymentModalOpen}
          onCancel={() => setPaymentModalOpen(false)}
          footer={null}
          Style={{ textAlign: "center" }} // canh giữa text trong modal
        >
          <div className="flex flex-col items-center gap-2">
            <p className="mb-1">
              Năng lượng: <b>{energy}</b> kWh
            </p>
            <p className="mb-1">
              Số tiền phải trả: <b>{cost}</b>
            </p>

            <p className="mt-4 mb-2 font-semibold">
              Chọn phương thức thanh toán:
            </p>

            {/* Button nằm giữa modal */}
            <div className="mt-2 flex justify-center gap-4">
              {/* <Button
                onClick={() => {
                  message.success("Thanh toán tiền mặt thành công!");
                  setPaymentModalOpen(false);
                  navigate("/user/history");
                }}
              >
                Cash
              </Button> */}

              <Button
                type="primary"
                loading={paying}
                onClick={handlePayEWallet}
                
              >
                EPayWallet
              </Button>
            </div>
          </div>
        </Modal>
        {!loading && !sessionData && (
          <p className="text-gray-500 mt-4">Empty Seesion.</p>
        )}
      </Card>
    </div>
  );
}

export default SessionInfo;
