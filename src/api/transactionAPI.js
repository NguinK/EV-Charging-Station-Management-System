import axiosClient from "./axiosClient";

const transactionAPI = {
  confirmPayment: (transactionId, data) => axiosClient.post(`/api/transactions/${transactionId}/confirmPayment`, data),
  getTransactionFromSession: () => axiosClient.get(`/api/transactions/my-history`),
  getDriverTransactionHistory: (driverId) => axiosClient.get(`/api/transactions/getDriverTransactionHistory/${driverId}`),
};

export default transactionAPI;
