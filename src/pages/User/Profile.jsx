// src/pages/Profile.jsx
import React, { useEffect, useMemo, useState } from "react";
import { Card, Descriptions, Spin, message } from "antd";
import getProfileAPI from "../../api/getProfileAPI"; 
import subscriptionAPI from "../../api/subscriptionAPI";
const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [subscription, setSubscription] = useState(null);
  const [loading, setLoading] = useState(false);

  // Lấy thông tin user đã lưu khi login
  const userInfo = useMemo(
    () => JSON.parse(localStorage.getItem("userInfo")) || {},
    []
  );
  const driverId = userInfo.driverId; 
  const accountId = userInfo.accountId || userInfo.id; 

  useEffect(() => {
    const fetchData = async () => {
      if (!driverId) {
        message.error("Không tìm thấy driverId. Vui lòng đăng nhập lại.");
        return;
      }
      if (!accountId) {
        message.error("Không tìm thấy accountId. Vui lòng đăng nhập lại.");
        return;
      }

     try {
        setLoading(true);

        // Gọi song song profile + history subscription
        const [profileRes, subsHistoryRes] = await Promise.all([
          getProfileAPI.getProfile(driverId),
          subscriptionAPI.getDriverSubHistory(accountId),
        ]);

        setProfile(profileRes.data);

        const history = subsHistoryRes.data || [];

        let activeSub = null;
        if (Array.isArray(history) && history.length > 0) {
          // Ưu tiên bản ghi status = ACTIVE
          activeSub =
            history.find((s) => s.status === "ACTIVE") ||
            // nếu backend dùng endDate/null để đánh dấu active
            history.find((s) => !s.endDate) ||
            // fallback: lấy bản ghi mới nhất (cuối mảng)
            history[history.length - 1];
        }

        setSubscription(activeSub);
      } catch (error) {
        console.error("Lỗi tải dữ liệu profile/subscription:", error);
        message.error("Không thể tải thông tin hồ sơ hoặc gói đăng ký.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [driverId, accountId]);

  // 👉 Tên gói hiển thị: nếu không có thì Basic
  const subscriptionName =
    subscription?.name ||
    subscription?.planName ||
    subscription?.subscriptionName ||
    "Basic";

  return (
    <Card
      title="Profile"
      style={{ maxWidth: 800, margin: "0 auto", textAlign: "center" }}
    >
      {loading || !profile ? (
        <div style={{ textAlign: "center", padding: "24px 0" }}></div>
      ) : (
        <Descriptions bordered column={1} size="middle">
          <Descriptions.Item label="Full Name">
            {profile.fullName}
          </Descriptions.Item>
          <Descriptions.Item label="Date of Birth">
            {profile.dateOfBirth}
          </Descriptions.Item>
          <Descriptions.Item label="Address">
            {profile.address}
          </Descriptions.Item>
          <Descriptions.Item label="Driver License">
            {profile.driverLicense}
          </Descriptions.Item>
          <Descriptions.Item label="Vehicle Number">
            {profile.vehicleNumber}
          </Descriptions.Item>
          <Descriptions.Item label="Vehicle Type">
            {profile.vehicleType}
          </Descriptions.Item>
          <Descriptions.Item label="Phone">{profile.phone}</Descriptions.Item>
          <Descriptions.Item label="Email">{profile.email}</Descriptions.Item>
          <Descriptions.Item label="Subscription Plan">{subscriptionName}</Descriptions.Item>
        </Descriptions>
      )}
    </Card>
  );
};

export default Profile;
