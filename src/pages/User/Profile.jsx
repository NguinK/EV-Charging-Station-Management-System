// src/pages/Profile.jsx
import React, { useEffect, useMemo, useState } from "react";
import { Card, Descriptions, Spin, message } from "antd";
import getProfileAPI from "../../api/getProfileAPI"; // chỉnh lại path nếu khác

const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(false);

  // Lấy thông tin user đã lưu khi login
  const userInfo = useMemo(
    () => JSON.parse(localStorage.getItem("userInfo")) || {},
    []
  );
  const driverId = userInfo.driverId; // đảm bảo lúc login bạn đã lưu driverId vào đây

  useEffect(() => {
    const fetchProfile = async () => {
      if (!driverId) {
        message.error("Không tìm thấy driverId. Vui lòng đăng nhập lại.");
        return;
      }

      try {
        setLoading(true);
        const res = await getProfileAPI.getProfile(driverId);
        setProfile(res.data);
      } catch (error) {
        console.error("Lỗi lấy profile:", error);
        message.error("Không thể tải thông tin hồ sơ.");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [driverId]);

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
        </Descriptions>
      )}
    </Card>
  );
};

export default Profile;
