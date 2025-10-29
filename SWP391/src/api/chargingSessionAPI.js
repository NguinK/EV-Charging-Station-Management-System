import axiosClient from "./axiosClient";

const chargingSessionAPI = {
  startSession: (reservationId) => axiosClient.post(`/api/sessions/start/${reservationId}`),
  endSession: (sessionId) => axiosClient.post(`/api/sessions/end/${sessionId}`),
};

export default chargingSessionAPI;