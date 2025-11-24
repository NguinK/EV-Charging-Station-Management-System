import axiosClient from "./axiosClient";

const transactionAPI = {
  confirmPayment: (transactionId,data) => axiosClient.post(`/api/transactions/${transactionId}/pay-EWallet`,data, { timeout: 5000 }),
  getTransactionFromSession: (sessionId) =>axiosClient.get(`/api/transactions/getTransactionfromSession/${sessionId}`),
 getMyHistory: () =>axiosClient.get(`/api/transactions/my-history`),
};

export default transactionAPI;
