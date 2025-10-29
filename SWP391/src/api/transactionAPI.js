import axiosClient from "./axiosClient";

const transactionAPI = {
  confirmPayment: (transactionId, data) => axiosClient.post(`/api/transactions/${transactionId}/confirmPayment`, data),
  getTransactionFromSession: (sessionId) => axiosClient.get(`/api/transactions/getTransactionfromSession/${sessionId}`),
  getDriverTransactionHistory: (driverId) => axiosClient.get(`/api/transactions/getDriverTransactionHistory/${driverId}`),
};

export default transactionAPI;
