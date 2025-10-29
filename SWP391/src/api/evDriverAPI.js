import axiosClient from "./axiosClient";

const evDriverAPI = {
  getDriverTransactions: (driverId) => axiosClient.get(`/drivers/transactions/${driverId}`),
  getDriverProfile: (driverId) => axiosClient.get(`/drivers/getProfile/${driverId}`),
};

export default evDriverAPI;
