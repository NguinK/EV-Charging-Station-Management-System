// src/api/driversAPI.js
 // axios instance bạn đã setup
import axiosClient from "./axiosClient";

const driversAPI = {
  /** Lấy lịch sử giao dịch của Driver */
  getTransactions: (driverId) => {
    return axiosClient.get(`/drivers/transactions/${driverId}`);
  },

  /** Lấy thông tin profile Driver */
  getProfile: (driverId) => {
    return axiosClient.get(`/drivers/getProfile/${driverId}`);
  },
};

export default driversAPI;
