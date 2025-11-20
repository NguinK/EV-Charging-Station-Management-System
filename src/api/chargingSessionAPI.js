import axiosClient from "./axiosClient";

const chargingSessionAPI = {
   // ⚡ Bắt đầu phiên sạc
  startSession: (reservationId, startSoc = 30) =>
    axiosClient.post(`/drivers/sessions/start/${reservationId}?startSoc=${startSoc}`),

  // 🔌 Kết thúc phiên sạc
  endManual: (sessionId) =>
    axiosClient.put(`/drivers/sessions/${sessionId}/endManual`),

 getByReservation: (id) => 
    axiosClient.get(`/drivers/sessions/sessions/${id}/status`),
 };
export default chargingSessionAPI;