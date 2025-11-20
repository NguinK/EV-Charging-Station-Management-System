import axiosClient from "./axiosClient";

const walletAPI = { 
  createWallet: (accountId) =>
    axiosClient.post(`/api/wallets/${accountId}/create`),
deposit: (driverId, amount, description) => {
    return axiosClient.post(`/api/wallets/${driverId}/deposit`, null, {
      params: { amount, description },
    });
  },
  getWallet: (accountId) => {
    return axiosClient.get(`/api/wallets/${accountId}`);
  },
};
export default walletAPI;