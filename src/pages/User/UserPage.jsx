import React, { useEffect, useMemo, useState } from "react";
import {
  DesktopOutlined,
  PieChartOutlined,
  WalletOutlined,
  LogoutOutlined,
  CarOutlined,
  UserOutlined,
} from "@ant-design/icons";
import {
  Layout,
  Avatar,
  message,
  Menu,
  Space,
  Button,
  Typography,
  Badge,
} from "antd";
import { Outlet, useNavigate, useLocation } from "react-router-dom";

import { useDispatch, useSelector } from "react-redux";
import { fetchWalletBalance } from "../../features/walletSlice";

const { Title, Text } = Typography;
const { Header, Sider, Content } = Layout;

export default function UserPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();
  const walletBalance = useSelector((state) => state.wallet.balance);

  const userInfo = useMemo(
    () => JSON.parse(localStorage.getItem("userInfo")) || {},
    []
  );
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) {
      message.warning("Bạn cần đăng nhập để truy cập dashboard!");
      navigate("/login");
    }
  }, [token, navigate]);
  const accountId = userInfo.accountId || userInfo.id;

  useEffect(() => {
    if (!token) return;
    if (!accountId) return;

    dispatch(fetchWalletBalance(accountId));
  }, [dispatch, token, accountId]);

  const fullName = userInfo.fullName || "User";
  const email = userInfo.email || "example@gmail.com";

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");
    message.success("Logged out!");
    navigate("/");
  };

  const menuItems = [
    { key: "/user/charge", icon: <PieChartOutlined />, label: "Booking" },
    { key: "/user/session", icon: <CarOutlined />, label: "Session" },
    { key: "/user/history", icon: <DesktopOutlined />, label: "History" },
    { key: "/user/wallet", icon: <WalletOutlined />, label: "Wallet" },
    { key: "/user/profile", icon: <UserOutlined />, label: "Profile" },
  ];

  return (
    <Layout
      className="min-h-screen bg-gradient-to-br from-[#f9fafb] to-[#eef1f4]"
      style={{
        padding: "0 40px 40px 40px",
        gap: "36px",
        flexDirection: "column",
      }}
    >
      <Header
        style={{
          background: "transparent",
          padding: "50px 0 10px 0",
          display: "flex",
          alignItems: "center",
        }}
      >
        <img src="/ev_logo.png" alt="EV Station Logo" style={{ height: 75 }} />
      </Header>

      <Layout style={{ gap: "36px", flexDirection: "row" }}>
        <Sider
          width={250}
          style={{
            background: "#fff",
            border: "1px solid #e5e7eb",
            borderRadius: "18px",
            boxShadow: "0 12px 40px rgba(0,0,0,0.06)",
            height: "fit-content",
          }}
        >
          <div className="px-6 pt-5  border-b border-[#f0f0f0]">
            <Space size={15} align="center">
              <Avatar
                size={35}
                style={{
                  background: "linear-gradient(135deg,#22c55e,#16a34a)",
                  fontSize: "20px",
                  left: 10,
                }}
              >
                {fullName.charAt(0)}
              </Avatar>
              <div>
                <div className="font-semibold">{fullName}</div>
                <div className="text-gray-500 text-sm">{email}</div>

                {/* 👇 số dư ví */}
                <div className="text-sm font-semibold mt-1">
                  Balance:{" "}
                  {walletBalance != null
                    ? walletBalance.toLocaleString("vi-VN")
                    : "Đang tải..."}{" "}
                  đ
                </div>
              </div>
            </Space>
          </div>

          <Menu
            mode="inline"
            selectedKeys={[location.pathname]}
            items={menuItems}
            onClick={({ key }) => navigate(key)}
            className="!bg-transparent"
            style={{ padding: "16px 0" }}
          />

          <div className="px-5 pb-20 border-t border-[#f2f2f2]">
            <Button
              type="text"
              icon={<LogoutOutlined />}
              danger
              className=" justify-start"
              onClick={handleLogout}
              style={{ left: 13 }}
            >
              Log out
            </Button>
          </div>
        </Sider>

        <Content
          style={{
            flex: 1,
            background: "#fff",
            borderRadius: "28px",
            border: "1px solid #e5e7eb",
            boxShadow: "0 25px 55px rgba(0,0,0,0.05)",
            padding: "56px 64px",
          }}
        >
          <Outlet /> {/* ✅ Route con hiển thị ở đây */}
        </Content>
      </Layout>
    </Layout>
  );
}
