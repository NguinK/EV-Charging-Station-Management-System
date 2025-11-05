import React, { useEffect, useState } from "react";
import ModalMessage from "../../components/ModalMessage";
import { Card, Empty } from "antd";
import { useDispatch, useSelector } from "react-redux";
import dayjs from "dayjs";
import { getReservationDetails } from "../../features/reservationSlice";

function Charge() {
  const { bookings } = useSelector((state) => state.reservation);
  const dispatch = useDispatch();
  // const latestBooking = bookings.length > 0 ? bookings[0] : null;
  const [latestBooking, setLatestBooking] = useState(null);
  useEffect(() => {
    // Nếu đã có Redux (vừa đặt xong)
    if (bookings.length > 0) {
      setLatestBooking(bookings[0]);
    } else {
      // 🆕 Nếu F5 hoặc reload: lấy id cuối lưu trong localStorage hoặc cố định
      const stored = localStorage.getItem("latestBookingId");
      if (stored) {
        dispatch(getReservationDetails(stored))
          .unwrap()
          .then((res) => setLatestBooking(res))
          .catch((err) => console.error("Lỗi khi load reservation:", err));
      }
    }
  }, [bookings, dispatch]);
  return (
    <div className="w-full flex justify-center mt-10">
      <Card
        title="Reservation"
        size="default"
        className="w-[500px] h-auto outline-none focus:outline-none shadow-lg"
      >
        {latestBooking ? (
          <div className="flex flex-col items-center gap-4 select-none">
            <img
              src="/ev_logo.png"
              alt="booking success"
              className="w-28 h-auto pointer-events-none mb-2"
              draggable="false"
            />

            <div className="text-center text-gray-800">
              <h2 className="font-bold text-lg text-green-600 mb-2">
                🎉 Đặt xe thành công!
              </h2>

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
        ) : (
          <div className="flex flex-col items-center justify-center gap-3">
            <Empty
              description="Bạn chưa có đặt xe nào"
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
