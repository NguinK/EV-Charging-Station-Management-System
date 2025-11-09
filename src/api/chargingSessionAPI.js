import axiosClient from "./axiosClient";

const chargingSessionAPI = {
   // ⚡ Bắt đầu phiên sạc
  startSession: (reservationId, startSoc = 30) =>
    axiosClient.post(`/sessions/start/${reservationId}?startSoc=${startSoc}`),

  // 🔌 Kết thúc phiên sạc
  endSession: (sessionId) =>
    axiosClient.put(`/sessions/${sessionId}/endManual`),

 getByReservation: (id) => 
    axiosClient.get(`/sessions/sessions/${id}/status`),
 };
export default chargingSessionAPI;