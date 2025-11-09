import React, { useEffect, useMemo, useRef, useState } from "react";
import ModalMessage from "../../components/ModalMessage";
import { Card, Empty, message, Spin } from "antd";
import { useDispatch, useSelector } from "react-redux";
import dayjs from "dayjs";
import { getReservationDetails } from "../../features/reservationSlice";
import { QRCodeCanvas } from "qrcode.react";
import {  useSearchParams } from "react-router-dom";
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
  const [sessionInfo, setSessionInfo] = useState(null);
  const [loading, setLoading] = useState(false);
  const hasStarted = useRef(false);
  // const navigate = useNavigate();

  const [params] = useSearchParams();
  const reservationIdFromQR = params.get("reservationId");
  const id  = params.get("id");

  // 🧭 Khi load trang: nếu có param reservationId -> gọi API startSession
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
          message.success("Bắt đầu sạc thành công!");
          setSessionInfo(res.data);
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

  useEffect(() => {
  const fetchSession = async () => {
    if (!id) return;
    try {
      setLoading(true);
      const res = await chargingSessionAPI.getByReservation(id);
      if (res.data) {
        console.log("🔁 Session hiện có:", res.data);
        setSessionInfo(res.data);
      }
    } catch (err) {
      console.log("⚠️ Chưa có session đang hoạt động:", err.response?.data);
    } finally {
      setLoading(false);
    }
  };

  fetchSession();
}, [id]);

  // 2) Nếu KHÔNG quét QR: lấy từ Redux; nếu Redux rỗng (sau reload) thì lấy từ localStorage và gọi API
  useEffect(() => {
    if (bookings && bookings.length > 0) {
      const newest = bookings[0]; // giả định mảng đã sort mới nhất trước
      setLatestBooking(newest);
      // LƯU lại id vào localStorage để lần sau reload còn biết mà lấy
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
    if (latestBooking?.status === "USED") {
      message.info("Phiên đặt đã được sử dụng. Vui lòng đặt lại.");
      setLatestBooking(null); // ✅ Xóa booking để quay lại giao diện modal
      localStorage.removeItem(userScopedKey); // ✅ Xóa trong localStorage để không load lại
    }
  }, [latestBooking, userScopedKey]);

  // ✅ URL để hiển thị QR code (chỉ khi có booking)
  const qrUrl = latestBooking
    ? `http://192.168.1.64:5173/user/charge?reservationId=${latestBooking.reservationId}`
    : "";

  return (
    <div className="w-full flex justify-center mt-10">
      <Card
        title="Reservation"
        size="default"
        className="w-[500px] h-auto outline-none focus:outline-none shadow-lg"
      >
        {/* 🟢 Nếu đang bắt đầu sạc từ QR */}
        {loading && (
          <div className="flex justify-center items-center py-10">
            <Spin tip="Đang khởi tạo phiên sạc..." />
          </div>
        )}

        {!loading && sessionInfo && (
          <div className="flex flex-col items-center text-center">
            <img
              src="/ev_logo.png"
              alt="charging"
              className="w-28 h-auto mb-3"
            />
            <h2 className="text-green-600 font-bold text-lg mb-2">
              🔌 Phiên sạc đang hoạt động
            </h2>
            <p>
              <b>Trạm sạc:</b> {sessionInfo.stationName}
            </p>
            <p>
              <b>Trụ sạc:</b> {sessionInfo.pointCode}
            </p>
            <p>
              <b>Bắt đầu lúc:</b>{" "}
              {dayjs(sessionInfo.startTime).format("YYYY-MM-DD HH:mm")}
            </p>
            <p>
              <b>Trạng thái:</b> {sessionInfo.status}
            </p>
          </div>
        )}
        {/* !loading && !sessionInfo && latestBooking ?  */}
        {!loading && !sessionInfo && !reservationIdFromQR && latestBooking ? (
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
              <QRCodeCanvas value={qrUrl} size={180} includeMargin level="M" />
              <p className="mt-2 text-sm text-gray-400">
                Quét mã này bằng điện thoại để bắt đầu phiên sạc.
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
        {!loading && !latestBooking && !sessionInfo && (
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

// // function Charge() {
// //   const dispatch = useDispatch();
// //   const { bookings } = useSelector((state) => state.reservation);
// //   const { info: sessionInfo, loading } = useSelector((state) => state.session);
// //   const [params] = useSearchParams();
// //   const reservationIdFromQR = params.get("reservationId");
// //   const hasStarted = useRef(false);

// //   const userInfo = useMemo(
// //     () => JSON.parse(localStorage.getItem("userInfo") || "{}"),
// //     []
// //   );

// //   // 🧭 Nếu quét QR => bắt đầu sạc
// //   useEffect(() => {
// //     if (reservationIdFromQR && !hasStarted.current) {
// //       hasStarted.current = true;
// //       const token = localStorage.getItem("token");
// //       if (!token) {
// //         message.error("Chưa đăng nhập, không thể bắt đầu phiên sạc!");
// //         return;
// //       }
// //       const startSession = async () => {
// //         try {
// //           const res = await chargingSessionAPI.startSession(reservationIdFromQR, 30);
// //           message.success("Bắt đầu sạc thành công!");
// //           dispatch(setSession(res.data));
// //         } catch (error) {
// //           message.error("Không thể bắt đầu phiên sạc!");
// //           console.error("Lỗi start session:", error);
// //         }
// //       };
// //       startSession();
// //     }
// //   }, [reservationIdFromQR, dispatch]);

// //   // 🧩 Nếu không có QR => lấy booking mới nhất từ Redux
// //   useEffect(() => {
// //     if (bookings && bookings.length > 0) {
// //       const newest = bookings[0];
// //       if (newest?.reservationId) {
// //         dispatch(fetchSessionByReservation(newest.reservationId));
// //       }
// //     }
// //   }, [bookings, dispatch]);

// //   // 🔁 Realtime cập nhật session mỗi 5 giây
// //   useEffect(() => {
// //     let interval;
// //     if (sessionInfo?.id) {
// //       interval = setInterval(async () => {
// //         try {
// //           const res = await chargingSessionAPI.getByReservation(sessionInfo.reservationId);
// //           dispatch(setSession(res.data));
// //         } catch (err) {
// //           console.warn("Realtime update lỗi:", err.message);
// //         }
// //       }, 5000);
// //     }
// //     return () => clearInterval(interval);
// //   }, [sessionInfo, dispatch]);

// //   const latestBooking = bookings?.[0];
// //   const qrUrl = latestBooking
// //     ? `http://192.168.1.64:5173/user/charge?reservationId=${latestBooking.reservationId}`
// //     : "";

// //   return (
// //     <div className="w-full flex justify-center mt-10">
// //       <Card
// //         title="Reservation / Charging Session"
// //         size="default"
// //         className="w-[500px] shadow-lg"
// //       >
// //         {loading && (
// //           <div className="flex justify-center items-center py-10">
// //             <Spin tip="Đang tải dữ liệu..." />
// //           </div>
// //         )}

// //         {/* 🔌 Nếu có phiên sạc */}
// //         {!loading && sessionInfo && (
// //           <div className="flex flex-col items-center text-center">
// //             <img src="/ev_logo.png" alt="charging" className="w-28 h-auto mb-3" />
// //             <h2 className="text-green-600 font-bold text-lg mb-2">
// //               🔌 Phiên sạc đang hoạt động
// //             </h2>
// //             <p><b>Trạm sạc:</b> {sessionInfo.stationName}</p>
// //             <p><b>Trụ sạc:</b> {sessionInfo.pointCode}</p>
// //             <p><b>Bắt đầu:</b> {dayjs(sessionInfo.startTime).format("YYYY-MM-DD HH:mm")}</p>
// //             <p><b>Kết thúc:</b> {sessionInfo.endTime ? dayjs(sessionInfo.endTime).format("YYYY-MM-DD HH:mm") : "Đang sạc..."}</p>
// //             <p><b>Start SoC:</b> {sessionInfo.startSoc}%</p>
// //             <p><b>End SoC:</b> {sessionInfo.endSoc}%</p>
// //             <p><b>Năng lượng đã tiêu thụ:</b> {sessionInfo.energyConsumed} kWh</p>
// //             <p><b>Chi phí:</b> {sessionInfo.cost} VND</p>
// //             <p><b>Trạng thái:</b> {sessionInfo.status}</p>
// //           </div>
// //         )}

// //         {/* 🎉 Nếu chỉ có booking mà chưa quét QR */}
// //         {!loading && !sessionInfo && latestBooking && (
// //           <div className="flex flex-col items-center gap-4">
// //             <img src="/ev_logo.png" alt="booking" className="w-28 h-auto mb-2" />
// //             <h2 className="font-bold text-lg text-green-600">🎉 Đặt xe thành công!</h2>
// //             <QRCodeCanvas value={qrUrl} size={180} includeMargin level="M" />
// //             <p><b>Mã đặt:</b> {latestBooking.reservationId}</p>
// //             <p><b>Trạm sạc:</b> {latestBooking.stationName}</p>
// //             <p><b>Cổng sạc:</b> {latestBooking.connectorType}</p>
// //             <p><b>Trụ sạc:</b> {latestBooking.chargingPointId}</p>
// //             <p><b>Trạng thái:</b> {latestBooking.status}</p>
// //           </div>
// //         )}

// //         {/* 🕳 Nếu chưa có booking */}
// //         {!loading && !sessionInfo && !latestBooking && (
// //           <div className="flex flex-col items-center justify-center gap-3">
// //             <Empty description="Bạn chưa có đặt xe nào" image={Empty.PRESENTED_IMAGE_SIMPLE} />
// //             <div className="mt-4 w-full flex justify-center">
// //               <ModalMessage />
// //             </div>
// //           </div>
// //         )}
// //       </Card>
// //     </div>
// //   );
// // }

// // export default Charge;

// // import React, { useEffect, useMemo, useRef } from "react";
// // import ModalMessage from "../../components/ModalMessage";
// // import { Card, Empty, message, Spin } from "antd";
// // import { useDispatch, useSelector } from "react-redux";
// // import dayjs from "dayjs";
// // import { getReservationDetails } from "../../features/reservationSlice";
// // import { QRCodeCanvas } from "qrcode.react";
// // import { useSearchParams } from "react-router-dom";
// // import chargingSessionAPI from "../../api/chargingSessionAPI";
// // import {
// //   fetchSessionByReservation,
// //   setSession,
// // } from "../../features/sessionSlice";

// // function Charge() {
// //   const dispatch = useDispatch();
// //   const { bookings } = useSelector((state) => state.reservation);
// //   const { info: sessionInfo, loading } = useSelector((state) => state.session);

// //   const [params] = useSearchParams();
// //   const reservationIdFromQR = params.get("reservationId");
// //   const hasStarted = useRef(false);

// //   const userInfo = useMemo(
// //     () => JSON.parse(localStorage.getItem("userInfo") || "{}"),
// //     []
// //   );

// //   // 🧭 Nếu quét QR -> bắt đầu phiên sạc
// //   useEffect(() => {
// //     if (reservationIdFromQR && !hasStarted.current) {
// //       hasStarted.current = true;

// //       const token = localStorage.getItem("token");
// //       if (!token) {
// //         message.error("Chưa đăng nhập, không thể bắt đầu phiên sạc!");
// //         return;
// //       }

// //       const startSession = async () => {
// //         try {
// //           const res = await chargingSessionAPI.startSession(
// //             reservationIdFromQR,
// //             30 // startSoc giả định, bạn có thể thay
// //           );
// //           message.success("Bắt đầu sạc thành công!");
// //           dispatch(setSession(res.data));
// //         } catch (error) {
// //           message.error("Không thể bắt đầu phiên sạc!");
// //           console.error("Lỗi start session:", error);
// //         }
// //       };

// //       startSession();
// //     }
// //   }, [reservationIdFromQR, dispatch]);

// //   // 🧩 Nếu không quét QR -> lấy booking mới nhất từ Redux và fetch session nếu có
// //   useEffect(() => {
// //     const loadExistingSession = async () => {
// //       try {
// //         // Nếu có booking gần nhất thì lấy reservationId
// //         let reservationId = bookings?.[0]?.reservationId;

// //         // Nếu không có trong Redux (vì reload), thử gọi API backend /drivers/reservations/latest
// //         if (!reservationId) {
// //           const latestRes = await dispatch(getReservationDetails()).unwrap();
// //           reservationId = latestRes?.reservationId;
// //         }

// //         // Nếu có reservationId thì kiểm tra session tương ứng
// //         if (reservationId) {
// //           const res = await chargingSessionAPI.getByReservation(reservationId);
// //           if (res?.data) {
// //             dispatch(setSession(res.data));
// //             console.log("🔁 Đã khôi phục phiên sạc sau F5:", res.data);
// //           }
// //         }
// //       } catch (err) {
// //         console.log("❌ Không có session nào đang hoạt động:", err.message);
// //       }
// //     };

// //     loadExistingSession();
// //   }, [dispatch, bookings]);

// //   // 🔁 Cập nhật realtime mỗi 5 giây nếu đang có session
// //   useEffect(() => {
// //     let interval;
// //     if (sessionInfo?.reservationId) {
// //       interval = setInterval(async () => {
// //         try {
// //           const res = await chargingSessionAPI.getByReservation(
// //             sessionInfo.reservationId
// //           );
// //           dispatch(setSession(res.data));
// //         } catch (err) {
// //           console.warn("Realtime update lỗi:", err.message);
// //         }
// //       }, 5000);
// //     }
// //     return () => clearInterval(interval);
// //   }, [sessionInfo, dispatch]);

// //   const latestBooking = bookings?.[0];

// //   const qrUrl = latestBooking
// //     ? `http://192.168.1.64:5173/user/charge?reservationId=${latestBooking.reservationId}`
// //     : "";

// //   return (
// //     <div className="w-full flex justify-center mt-10">
// //       <Card
// //         title="Thông tin đặt xe / Phiên sạc"
// //         size="default"
// //         className="w-[500px] h-auto shadow-lg"
// //       >
// //         {/* Loading */}
// //         {loading && (
// //           <div className="flex justify-center items-center py-10">
// //             <Spin tip="Đang tải dữ liệu..." />
// //           </div>
// //         )}

// //         {/* 🔌 Nếu có session (đang sạc hoặc đã hoàn tất) */}
// //         {!loading && sessionInfo && (
// //           <div className="flex flex-col items-center text-center">
// //             <img
// //               src="/ev_logo.png"
// //               alt="charging"
// //               className="w-28 h-auto mb-3"
// //             />
// //             <h2 className="text-green-600 font-bold text-lg mb-2">
// //               🔌 Phiên sạc đang hoạt động
// //             </h2>

// //             <p>
// //               <b>Trạm sạc:</b> {sessionInfo.stationName}
// //             </p>
// //             <p>
// //               <b>Trụ sạc:</b> {sessionInfo.pointCode}
// //             </p>
// //             <p>
// //               <b>Bắt đầu:</b>{" "}
// //               {sessionInfo.startTime
// //                 ? dayjs(sessionInfo.startTime).format("YYYY-MM-DD HH:mm")
// //                 : "Chưa có"}
// //             </p>
// //             <p>
// //               <b>Kết thúc:</b>{" "}
// //               {sessionInfo.endTime
// //                 ? dayjs(sessionInfo.endTime).format("YYYY-MM-DD HH:mm")
// //                 : "Đang sạc..."}
// //             </p>
// //             <p>
// //               <b>Start SoC:</b> {sessionInfo.startSoc ?? 0}%
// //             </p>
// //             <p>
// //               <b>End SoC:</b> {sessionInfo.endSoc ?? 0}%
// //             </p>
// //             <p>
// //               <b>Năng lượng tiêu thụ:</b> {sessionInfo.energyConsumed ?? 0} kWh
// //             </p>
// //             <p>
// //               <b>Chi phí:</b> {sessionInfo.cost ?? 0} VND
// //             </p>
// //             <p>
// //               <b>Trạng thái:</b> {sessionInfo.status}
// //             </p>
// //           </div>
// //         )}

// //         {/* 🎉 Nếu có booking mà chưa quét QR */}
// //         {!loading && !sessionInfo && latestBooking && (
// //           <div className="flex flex-col items-center gap-4 select-none">
// //             <img
// //               src="/ev_logo.png"
// //               alt="booking success"
// //               className="w-28 h-auto pointer-events-none mb-2"
// //               draggable="false"
// //             />
// //             <div className="text-center text-gray-800">
// //               <h2 className="font-bold text-lg text-green-600 mb-2">
// //                 🎉 Đặt xe thành công!
// //               </h2>
// //               <QRCodeCanvas value={qrUrl} size={180} includeMargin level="M" />
// //               <p className="mt-2 text-sm text-gray-400">
// //                 Quét mã này bằng điện thoại để bắt đầu phiên sạc.
// //               </p>

// //               <p>
// //                 <b>Mã đặt:</b> {latestBooking.reservationId || "N/A"}
// //               </p>
// //               <p>
// //                 <b>Trạm sạc:</b> {latestBooking.stationName || "Chưa có"}
// //               </p>
// //               <p>
// //                 <b>Cổng sạc:</b>{" "}
// //                 {latestBooking.connectorType || "Không xác định"}
// //               </p>
// //               <p>
// //                 <b>Trụ sạc:</b> {latestBooking.chargingPointId || "Không có"}
// //               </p>
// //               <p>
// //                 <b>Trạng thái:</b> {latestBooking.status || "Chưa xác định"}
// //               </p>
// //               <p>
// //                 <b>Thời gian hết hạn:</b>{" "}
// //                 {latestBooking.expireTime
// //                   ? dayjs(latestBooking.expireTime).format("YYYY-MM-DD HH:mm")
// //                   : "Chưa có"}
// //               </p>
// //             </div>
// //           </div>
// //         )}

// //         {/* 🔴 Nếu chưa có booking */}
// //         {!loading && !latestBooking && !sessionInfo && (
// //           <div className="flex flex-col items-center justify-center gap-3">
// //             <Empty
// //               description="Bạn chưa có đặt xe nào"
// //               image={Empty.PRESENTED_IMAGE_SIMPLE}
// //             />
// //             <div className="mt-4 w-full flex justify-center">
// //               <ModalMessage />
// //             </div>
// //           </div>
// //         )}
// //       </Card>
// //     </div>
// //   );
// // }

// // export default Charge;
// import React, { useEffect, useMemo, useRef, useState } from "react";
// import { Card, Empty, message, Spin, Modal, Button } from "antd";
// import { QRCodeCanvas } from "qrcode.react";
// import { useDispatch, useSelector } from "react-redux";
// import { useSearchParams } from "react-router-dom";
// import dayjs from "dayjs";

// import ModalMessage from "../../components/ModalMessage";
// import chargingSessionAPI from "../../api/chargingSessionAPI";
// import {
//   fetchSessionByReservation,
//   setSession,
//   clearSession,
// } from "../../features/sessionSlice";
// import { getReservationDetails } from "../../features/reservationSlice";

// function Charge() {
//   const dispatch = useDispatch();
//   const { bookings } = useSelector((state) => state.reservation);
//   const { info: sessionInfo, loading } = useSelector((state) => state.session);

//   const [params] = useSearchParams();
//   const reservationIdFromQR = params.get("reservationId");
//   const hasStarted = useRef(false);

//   const [elapsed, setElapsed] = useState(0);
//   const [isStopping, setIsStopping] = useState(false);

//   // const userInfo = useMemo(
//   //   () => JSON.parse(localStorage.getItem("userInfo") || "{}"),
//   //   []
//   // );

//   // 🧭 Nếu quét QR -> gọi POST /sessions/start
//   useEffect(() => {
//     if (reservationIdFromQR && !hasStarted.current) {
//       hasStarted.current = true;
//       const token = localStorage.getItem("token");
//       if (!token) {
//         message.error("Chưa đăng nhập, không thể bắt đầu phiên sạc!");
//         return;
//       }

//       const startSession = async () => {
//         try {
//           const res = await chargingSessionAPI.startSession(
//             reservationIdFromQR,
//             30 // startSoc giả định
//           );
//           message.success("Bắt đầu sạc thành công!");
//           dispatch(setSession(res.data));
//         } catch (error) {
//           message.error("Không thể bắt đầu phiên sạc!");
//           console.error("Lỗi start session:", error);
//         }
//       };
//       startSession();
//     }
//   }, [reservationIdFromQR, dispatch]);

//   // 🧩 Nếu không có QR, lấy booking mới nhất trong Redux để gọi getByReservation
//   useEffect(() => {
//     const loadSession = async () => {
//       try {
//         const reservationId = bookings?.[0]?.reservationId;
//         if (reservationId) {
//           const res = await chargingSessionAPI.getByReservation(reservationId);
//           if (res?.data) {
//             dispatch(setSession(res.data));
//           }
//         }
//       } catch (err) {
//         console.log("⚠️ Không có session nào:", err.message);
//       }
//     };
//     loadSession();
//   }, [bookings, dispatch]);

//   // 🔁 Realtime update mỗi 5 giây (GET /status)
//   useEffect(() => {
//     let interval;
//     if (sessionInfo?.sessionId && sessionInfo.status === "CHARGING") {
//       interval = setInterval(async () => {
//         try {
//           const res = await chargingSessionAPI.getStatusById(
//             sessionInfo.sessionId
//           );
//           dispatch(setSession({ ...sessionInfo, ...res.data }));
//         } catch (err) {
//           console.warn("Realtime update lỗi:", err.message);
//         }
//       }, 5000);
//     }
//     return () => clearInterval(interval);
//   }, [sessionInfo, dispatch]);

//   // ⏱ Đếm thời gian realtime
//   useEffect(() => {
//     if (sessionInfo?.startTime && sessionInfo.status === "CHARGING") {
//       const timer = setInterval(() => {
//         const diff = Date.now() - new Date(sessionInfo.startTime).getTime();
//         setElapsed(diff);
//       }, 1000);
//       return () => clearInterval(timer);
//     }
//   }, [sessionInfo?.startTime, sessionInfo?.status]);

//   const formatTime = (ms) => {
//     const totalSeconds = Math.floor(ms / 1000);
//     const h = String(Math.floor(totalSeconds / 3600)).padStart(2, "0");
//     const m = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, "0");
//     const s = String(totalSeconds % 60).padStart(2, "0");
//     return `${h}:${m}:${s}`;
//   };

//   // ⚙️ Dừng sạc và thanh toán
//   const handleStopSession = () => {
//     Modal.confirm({
//       title: "Xác nhận dừng phiên sạc",
//       content: "Bạn có chắc muốn dừng sạc không?",
//       okText: "Dừng sạc",
//       cancelText: "Huỷ",
//       onOk: () => {
//         Modal.info({
//           title: "Chọn phương thức thanh toán",
//           content: (
//             <div className="flex gap-3 mt-3">
//               <Button
//                 type="primary"
//                 onClick={() => endManual("EWALLET")}
//                 loading={isStopping}
//               >
//                 Ví điện tử
//               </Button>
//               <Button onClick={() => endManual("BANKING")} loading={isStopping}>
//                 Ngân hàng
//               </Button>
//             </div>
//           ),
//           okButtonProps: { style: { display: "none" } },
//         });
//       },
//     });
//   };
//   //Cap nhat khung 
  

//   const endManual = async (method) => {
//     try {
//       setIsStopping(true);
//       await chargingSessionAPI.endManual(sessionInfo.sessionId, method);
//       message.success("Đã dừng phiên sạc và thanh toán thành công!");
//       dispatch(clearSession());
//     } catch (err) {
//       message.error("Không thể dừng phiên sạc!");
//       console.error("Lỗi endManual:", err);
//     } finally {
//       setIsStopping(false);
//       Modal.destroyAll();
//     }
//   };

//   const latestBooking = bookings?.[0];
//   const qrUrl = latestBooking
//     ? `http://192.168.1.64:5173/user/charge?reservationId=${latestBooking.reservationId}`
//     : "";

//   return (
//     <div className="w-full flex justify-center mt-10">
//       <Card
//         title="Thông tin đặt xe / Phiên sạc"
//         size="default"
//         className="w-[500px] h-auto shadow-lg"
//       >
//         {loading && (
//           <div className="flex justify-center items-center py-10">
//             <Spin tip="Đang tải dữ liệu..." />
//           </div>
//         )}

//         {/* 🔌 Nếu có session */}
//         {!loading && sessionInfo && (
//           <div className="flex flex-col items-center text-center">
//             <img
//               src="/ev_logo.png"
//               alt="charging"
//               className="w-28 h-auto mb-3"
//             />
//             <h2 className="text-green-600 font-bold text-lg mb-2">
//               🔌 Phiên sạc đang hoạt động
//             </h2>

//             <p>
//               <b>Session ID:</b> {sessionInfo.sessionId}
//             </p>
//             <p>
//               <b>Trạng thái:</b> {sessionInfo.status}
//             </p>
//             <p>
//               <b>Thời gian sạc:</b> {formatTime(elapsed)}
//             </p>
//             <p>
//               <b>SoC:</b> {sessionInfo.soc ?? 0}%
//             </p>
//             <p>
//               <b>Năng lượng:</b>{" "}
//               {sessionInfo.energyConsumed ?? sessionInfo.energy ?? 0} kWh
//             </p>
//             <p>
//               <b>Chi phí:</b> {sessionInfo.cost ?? 0} VND
//             </p>
//             <p>
//               <b>Bắt đầu:</b>{" "}
//               {dayjs(sessionInfo.startTime).format("YYYY-MM-DD HH:mm")}
//             </p>

//             {sessionInfo.status === "CHARGING" && (
//               <Button
//                 type="primary"
//                 danger
//                 className="mt-4"
//                 onClick={handleStopSession}
//               >
//                 Dừng phiên sạc
//               </Button>
//             )}
//           </div>
//         )}

//         {/* 🎉 Nếu có booking nhưng chưa sạc */}
//         {!loading && !sessionInfo && latestBooking && (
//           <div className="flex flex-col items-center gap-4 select-none">
//             <img
//               src="/ev_logo.png"
//               alt="booking success"
//               className="w-28 h-auto pointer-events-none mb-2"
//               draggable="false"
//             />
//             <h2 className="font-bold text-lg text-green-600 mb-2">
//               🎉 Đặt xe thành công!
//             </h2>
//             <QRCodeCanvas value={qrUrl} size={180} includeMargin level="M" />
//             <p className="mt-2 text-sm text-gray-400">
//               Quét mã này bằng điện thoại để bắt đầu phiên sạc.
//             </p>
//             <p>
//               <b>Mã đặt:</b> {latestBooking.reservationId}
//             </p>
//             <p>
//               <b>Trạm sạc:</b> {latestBooking.stationName}
//             </p>
//             <p>
//               <b>Trạng thái:</b> {latestBooking.status}
//             </p>
//             <p>
//               <b>Cổng sạc:</b> {latestBooking.connectorType || "Không xác định"}
//             </p>
//             <p>
//               <b>Trụ sạc:</b> {latestBooking.chargingPointId || "Không có"}
//             </p>{" "}
//             <p>
//               <b>Trạng thái:</b> {latestBooking.status || "Chưa xác định"}
//             </p>
//             <p>
//               <b>Thời gian:</b>{" "}
//               {latestBooking.expireTime
//                 ? dayjs(latestBooking.expireTime).format("YYYY-MM-DD HH:mm")
//                 : "Chưa có"}
//             </p>
//           </div>
//         )}

//         {/* ❌ Nếu chưa có booking */}
//         {!loading && !latestBooking && !sessionInfo && (
//           <div className="flex flex-col items-center justify-center gap-3">
//             <Empty
//               description="Bạn chưa có đặt xe nào"
//               image={Empty.PRESENTED_IMAGE_SIMPLE}
//             />
//             <div className="mt-4 w-full flex justify-center">
//               <ModalMessage />
//             </div>
//           </div>
//         )}
//       </Card>
//     </div>
//   );
// }

// export default Charge;
