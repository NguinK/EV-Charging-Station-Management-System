import React from "react";
import ModalMessage from "../../components/ModalMessage";
import { Card, Empty } from "antd";
import { useSelector } from "react-redux";
import dayjs from "dayjs";

function Charge() {
  const { bookings } = useSelector((state) => state.reservation);
  const latestBooking = bookings.length > 0 ? bookings[0] : null;
  return (
    <div className="w-full flex justify-center mt-10">
      <Card
        title="Thông tin đặt xe"
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
                <b>Mã đặt:</b>{" "}
                {latestBooking.reservationCode || "N/A"}
              </p>
              <p>
                <b>Trạm sạc:</b> {latestBooking.stationName || "Chưa có"}
              </p>
              <p>
                <b>Cổng sạc:</b> {latestBooking.connectorType}
              </p>
              <p>
                <b>Thời gian:</b>{" "}
                {dayjs(latestBooking.startTime).format("YYYY-MM-DD HH:mm")}
              </p>
            </div>
          </div>
        ) : (
          <Empty
            description="Bạn chưa có đặt xe nào"
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          />
        )}
      </Card>
       <div className="mt-6">
        <ModalMessage />
      </div>
    </div>
  );
}

export default Charge;
