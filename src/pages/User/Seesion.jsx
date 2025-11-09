// import React, { useEffect, useState } from "react";
// import { Card, Spin, message } from "antd";
// import dayjs from "dayjs";
// import { useSearchParams } from "react-router-dom";
// import chargingSessionAPI from "../../api/chargingSessionAPI";

// function SessionInfo() {
//   const [params] = useSearchParams();
//   const sessionId = params.get("id");
//   const [sessionData, setSessionData] = useState(null);
//   const [loading, setLoading] = useState(false);

//   useEffect(() => {
//     const fetchSessionStatus = async () => {
//       if (!sessionId) return;
//       try {
//         setLoading(true);
//         const res = await chargingSessionAPI.getByReservation(sessionId);
//         if (res.data) {
//           setSessionData(res.data);
//         } else {
//           message.warning("Không có dữ liệu phiên sạc!");
//         }
//       } catch (err) {
//         message.error("Không thể lấy thông tin sạc!");
//         console.error(err);
//       } finally {
//         setLoading(false);
//       }
//     };

//     // Gọi ngay khi load và cập nhật mỗi 5 giây
//     fetchSessionStatus();
//     const interval = setInterval(fetchSessionStatus, 5000);
//     return () => clearInterval(interval);
//   }, [sessionId]);

//   return (
//     <div className="w-full flex justify-center mt-10">
//       <Card
//         title="🔋 Phiên sạc đang hoạt động"
//         size="default"
//         className="w-[500px] h-auto shadow-lg"
//       >
//         {loading && (
//           <div className="flex justify-center items-center py-10">
//             <Spin tip="Đang tải dữ liệu..." />
//           </div>
//         )}

//         {!loading && sessionData && (
//           <div className="text-center">
//             <img
//               src="/ev_logo.png"
//               alt="charging"
//               className="w-24 mx-auto mb-4"
//             />
//             <p>
//               <b>Session ID:</b> {sessionId}
//             </p>
//             <p>
//               ⚡ <b>SoC:</b> {sessionData.soc ?? "--"}%
//             </p>
//             <p>
//               🔋 <b>Năng lượng:</b> {sessionData.energy?.toFixed(2) ?? "--"} kWh
//             </p>
//             <p>
//               💰 <b>Chi phí:</b>{" "}
//               {sessionData.cost
//                 ? sessionData.cost.toLocaleString("vi-VN") + " đ"
//                 : "--"}
//             </p>
//             <p>
//               🟢 <b>Trạng thái:</b> {sessionData.status}
//             </p>
//           </div>
//         )}

//         {!loading && !sessionData && (
//           <p className="text-center text-gray-500">
//             Không tìm thấy phiên sạc nào.
//           </p>
//         )}
//       </Card>
//     </div>
//   );
// }

// export default SessionInfo;
import React, { useEffect, useState } from "react";
import { Card, Spin, message, Progress, Button } from "antd";
import { useNavigate, useSearchParams } from "react-router-dom";
import chargingSessionAPI from "../../api/chargingSessionAPI";
import { endSessionManual } from "../../features/sessionSlice";
import { useDispatch } from "react-redux";

function SessionInfo() {
  const [params] = useSearchParams();
  const sessionIdFromUrl = params.get("id");
  const [sessionId, setSessionId] = useState(sessionIdFromUrl || null);
  const [sessionData, setSessionData] = useState(null);
  const [loading, setLoading] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // 🧭 Khi mở trang: nếu không có id trên URL thì lấy từ localStorage
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

  // 🔁 Gọi API mỗi 5s để cập nhật dữ liệu
  useEffect(() => {
    if (!sessionId) return;

    const fetchSession = async () => {
      try {
        setLoading(true);
        const res = await chargingSessionAPI.getByReservation(sessionId);
        if (res.data) {
          setSessionData(res.data);

          // ✅ Lưu dữ liệu hiện tại vào localStorage
          localStorage.setItem(
            "activeSession",
            JSON.stringify({
              sessionId: sessionId,
              sessionData: res.data,
            })
          );

          // ✅ Nếu backend báo đã hoàn tất => xóa lưu trữ
          if (res.data.status === "COMPLETED" || res.data.status === "ENDED") {
            localStorage.removeItem("activeSession");
            message.success("🔋 Phiên sạc đã hoàn tất!");
          }
        }
      } catch (err) {
        console.error("Lỗi khi lấy thông tin sạc:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchSession();
    const interval = setInterval(fetchSession, 15000);
    return () => clearInterval(interval);
  }, [sessionId]);

  // 💡 Hiển thị progress SoC
  const soc = sessionData?.soc ?? 0;
  const cost = sessionData?.cost
    ? sessionData.cost.toLocaleString("vi-VN") + " đ"
    : "--";
  const energy = sessionData?.energy?.toFixed(2) ?? "--";
  const status = sessionData?.status || "UNKNOWN";

  const handleEndSession = async () => {
    if (!sessionId) return;
    try {
      await dispatch(endSessionManual(sessionId)).unwrap();
      message.success("⚡ Phiên sạc đã kết thúc!");
      localStorage.removeItem("activeSession");

      // 👉 Sau khi kết thúc, chuyển sang trang thanh toán
      navigate(`/user/charge`);
    } catch (err) {
      message.error("Không thể kết thúc phiên sạc!");
      console.error(err);
    }
  };
  return (
    <div className="w-full flex justify-center mt-10">
      <Card
        title="⚡ Seesion"
        className="w-[480px] shadow-lg text-center"
      >
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

            <p className="text-lg font-semibold mt-2">
              ⚙️ Trạng thái:{" "}
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
              🔋 <b>Năng lượng:</b> {energy} kWh
            </p>
            <p>
              💰 <b>Chi phí:</b> {cost}
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

        {!loading && !sessionData && (
          <p className="text-gray-500 mt-4">
            Empty Seesion.
          </p>
        )}
      </Card>
    </div>
  );
}

export default SessionInfo;
