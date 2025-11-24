import axiosClient from "./axiosClient";

const walletAPI = { 
  createWallet: (accountId) =>
    axiosClient.post(`/api/wallets/${accountId}/create`),
deposit: (accountId, amount, description) => {
    return axiosClient.post(`/api/wallets/${accountId}/deposit`, null, {
      params: { amount, description },
    });
  },
  getWallet: (accountId) => {
    return axiosClient.get(`/api/wallets/${accountId}`);
  },
    getTransactions: (accountId) => {
    return axiosClient.get(`/api/wallets/${accountId}/transactions`);
  },
};
export default walletAPI;