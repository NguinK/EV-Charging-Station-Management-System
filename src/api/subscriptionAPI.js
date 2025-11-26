import axiosClient from "./axiosClient";

const subscriptionAPI = {
  // POST /api/subscriptions/register/{accountId}
  registerForAccount: (accountId, data) => {
    return axiosClient.post(`/api/subscriptions/register/${accountId}`, data);
  },

  // POST /api/subscriptions/adminCreate
  adminCreate: (data) => {
    return axiosClient.post("/api/subscriptions/adminCreate", data);
  },

  // PUT /api/subscriptions/adminUpdate/{id}
  adminUpdate: (id, data) => {
    return axiosClient.put(`/api/subscriptions/adminUpdate/${id}`, data);
  },

  // GET /api/subscriptions/driverGetSubHistory/{accountId}
  getDriverSubHistory: (accountId) => {
    return axiosClient.get(
      `/api/subscriptions/driverGetSubHistory/${accountId}`
    );
  },

  // GET /api/subscriptions/adminGetAllSubs
  adminGetAllSubs: () => {
    return axiosClient.get("/api/subscriptions/adminGetAllSubs");
  },

  // GET /api/subscriptions/adminGetAllDriverSubs
  adminGetAllDriverSubs: () => {
    return axiosClient.get("/api/subscriptions/adminGetAllDriverSubs");
  },

  // DELETE /api/subscriptions/adminDelete/{id}
  adminDelete: (id) => {
    return axiosClient.delete(`/api/subscriptions/adminDelete/${id}`);
  },
};

export default subscriptionAPI;
