import React, { useEffect, useMemo, useRef, useState } from "react";
import ModalMessage from "../../components/ModalMessage";
import { Card, Empty, message, Spin } from "antd";
import { useDispatch, useSelector } from "react-redux";
import dayjs from "dayjs";
import { getReservationDetails } from "../../features/reservationSlice";
import { QRCodeCanvas } from "qrcode.react";
import { Navigate, useNavigate, useSearchParams } from "react-router-dom";
import chargingSessionAPI from "../../api/chargingSessionAPI";

function Charge() {
  const { bookings } = useSelector((state) => state.reservation);
  const dispatch = useDispatch();
  const userInfo = useMemo(
    () => JSON.parse(localStorage.getItem("userInfo") || "{}"),
    []
  );
  const userScopedKey = `latestBookingId_${userInfo?.id || "guest"}`;
  const [latestBooking, setLatestBooking] = useState(null);
  const [loading, setLoading] = useState(false);
  const hasStarted = useRef(false);

  const [params] = useSearchParams();
  const reservationIdFromQR = params.get("reservationId");
  const navigate = useNavigate();

  // 🧭 Nếu có param reservationId -> gọi API startSession (chỉ xử lý khởi tạo, không hiển thị session)
  useEffect(() => {
    if (reservationIdFromQR && !hasStarted.current) {
      hasStarted.current = true;
      const token = localStorage.getItem("token");
      if (!token) {
        message.error("Chưa đăng nhập, không thể bắt đầu phiên sạc!");
        return;
      }
      const startSession = async () => {
        try {
          setLoading(true);
          const res = await chargingSessionAPI.startSession(
            reservationIdFromQR,
            30
          );

          if (res.data?.id) {
            message.success("Bắt đầu sạc thành công!");
            // ⚡ Dùng sessionId thay vì reservationId
            navigate(`/user/session?id=${res.data.id}`);
          } else {
            message.error("Không tìm thấy sessionId trong phản hồi!");
          }
        } catch (error) {
          message.error("Không thể bắt đầu phiên sạc!");
          console.error("Lỗi start session:", error);
        } finally {
          setLoading(false);
        }
      };
      startSession();
    }
  }, [reservationIdFromQR]);

  // 🧭 Lấy thông tin Booking từ Redux hoặc localStorage
  useEffect(() => {
    if (bookings && bookings.length > 0) {
      const newest = bookings[0];
      setLatestBooking(newest);
      if (newest?.reservationId) {
        localStorage.setItem(userScopedKey, String(newest.reservationId));
      }
      return;
    }

    const storedId = localStorage.getItem(userScopedKey);
    if (storedId) {
      dispatch(getReservationDetails(storedId))
        .unwrap()
        .then((res) => setLatestBooking(res))
        .catch((err) => console.error("Lỗi khi load reservation:", err));
    }
  }, [bookings, dispatch, userScopedKey]);

  useEffect(() => {
    if (latestBooking?.status === "COMPLETED") {
      message.info("Phiên đặt đã được sử dụng. Vui lòng đặt lại.");
      setLatestBooking(null);
      localStorage.removeItem(userScopedKey);
    }
  }, [latestBooking, userScopedKey]);

  // ✅ Tạo QR để quét bắt đầu sạc
  const qrUrl = latestBooking
    ? `http://localhost:5173/user/charge?reservationId=${latestBooking.reservationId}`
    : "";

  return (
    <div className="w-full flex justify-center mt-10">
      <Card
        title="🗓️ Đặt lịch sạc"
        size="default"
        className="w-[480px] shadow-lg text-center"
      >
        {loading && (
          <div className="flex justify-center items-center py-10">
            <Spin tip="Đang khởi tạo phiên sạc..." />
          </div>
        )}

        {/* 🟢 Nếu có Booking */}
        {!loading && latestBooking ? (
          <div className="flex flex-col items-center gap-4 select-none">
            <img
              src="/ev_logo.png"
              alt="booking success"
              className="w-28 h-auto mb-2"
              draggable="false"
            />
            <div className="text-center text-gray-800">
              <h2 className="font-bold text-lg text-green-600 mb-2">
                🎉 Đặt lịch thành công!
              </h2>
              <QRCodeCanvas value={qrUrl} size={180} includeMargin level="M" />
              <p className="mt-2 text-sm text-gray-400">
                Quét mã này  để bắt đầu phiên sạc.
              </p>

              <p>
                <b>Mã đặt:</b> {latestBooking.reservationId || "N/A"}
              </p>
              <p>
                <b>Trạm sạc:</b> {latestBooking.stationName || "Chưa có"}
              </p>
              <p>
                <b>Cổng sạc:</b>{" "}
                {latestBooking.connectorType || "Không xác định"}
              </p>
              <p>
                <b>Trụ sạc:</b> {latestBooking.chargingPointId || "Không có"}
              </p>
              <p>
                <b>Trạng thái:</b> {latestBooking.status || "Chưa xác định"}
              </p>
              <p>
                <b>Thời gian:</b>{" "}
                {latestBooking.expireTime
                  ? dayjs(latestBooking.expireTime).format("YYYY-MM-DD HH:mm")
                  : "Chưa có"}
              </p>
            </div>
          </div>
        ) : null}

        {/* 🔴 Nếu chưa có booking */}
        {!loading && !latestBooking && (
          <div className="flex flex-col items-center justify-center gap-3">
            <Empty
              description="Reservation empty."
              image={Empty.PRESENTED_IMAGE_SIMPLE}
            />
            <div className="mt-4 w-full flex justify-center">
              <ModalMessage />
            </div>
          </div>
        )}
      </Card>
    </div>
  );
}

export default Charge;
