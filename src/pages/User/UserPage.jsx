import React, { useEffect, useMemo, useState } from "react";
import {
  DesktopOutlined,
  PieChartOutlined,
  WalletOutlined,
  LogoutOutlined,
} from "@ant-design/icons";
import {
  Layout,
  Avatar,
  message,
  Menu,
  Typography,
  Row,
  Col,
  Card,
  Space,
  Button,
} from "antd";
import { Outlet, useNavigate } from "react-router-dom";
import Charge from "./Charge";

const { Title, Text } = Typography;
const { Header, Sider, Content } = Layout;

export default function UserPage() {
  const navigate = useNavigate();
  const [selectedKey, setSelectedKey] = useState("booking");

  const userInfo = useMemo(
    () => JSON.parse(localStorage.getItem("userInfo")) || {},
    []
  );
  const token = localStorage.getItem("token");

  // ✅ Nếu chưa đăng nhập thì tự động redirect
  useEffect(() => {
    if (!token) {
      message.warning("Bạn cần đăng nhập để truy cập dashboard!");
      navigate("/login"); // hoặc navigate("/")
    }
  }, [token, navigate]);

  const fullName = userInfo.fullName || "User";
  const email = userInfo.email || "example@gmail.com";

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");
    message.success("Logged out!");
    navigate("/");
  };

  const menuItems = [
    { key: "booking", icon: <PieChartOutlined />, label: "Booking" ,  },
    { key: "history", icon: <DesktopOutlined />, label: "History" },
     { key: "Plan", icon: <WalletOutlined />, label: "Plan" },
     { key: "wallet", icon: <WalletOutlined />, label: "Wallet" },
  ];

  return (
    <Layout
      className="min-h-screen bg-gradient-to-br from-[#f9fafb] to-[#eef1f4] font-[Inter]"
      style={{
        padding: "0 40px 40px 40px", // chừa phần header
        gap: "36px",
        display: "flex",
        flexDirection: "column",
      }}
    >
      {/* ✅ HEADER */}
      <Header
        style={{
          background: "transparent",
          padding: "50px 0 10px 0",
          display: "flex",
          alignItems: "center",
          justifyContent: "flex-start",
          
        }}
      >
        <img
          src="/ev_logo.png" 
          alt="EV Station Logo"
          style={{
            height: 75,
            width: "auto",
            objectFit: "contain",
          }}
        />
      </Header>

      {/* ✅ MAIN CONTENT LAYOUT */}
      <Layout
        style={{
          gap: "36px",
          flexDirection: "row",
          display: "flex",
        }}
      >
        {/* ✅ SIDEBAR FLOATING */}
        <Sider
          width={250}
          style={{
            background: "#ffffff",
            border: "1px solid #e5e7eb",
            borderRadius: "18px",
            boxShadow: "0 12px 40px rgba(0,0,0,0.06)",
            overflow: "hidden",
            display: "flex",
            flexDirection: "column",
            justifyContent: "space-between",
            left:-15,
          }}
        >
          {/* USER INFO */}
          <div>
            <div
              onClick={() => setSelectedKey("profile")}
              className="px-6 pt-5 pb-10 border-b border-[#f0f0f0] cursor-pointer hover:bg-[#f9fafb] transition-all"
            >
              <Space size={15} align="center">
                <Avatar
                  size={30}
                  style={{
                    background: "linear-gradient(135deg,#22c55e,#16a34a)",
                    color: "#951c1c",
                    fontWeight: 500,
                    fontSize: "24px",
                    left : 5,
                    padding:20
                  }}
                >
                  {fullName.charAt(0)}
                </Avatar>
                <div className="flex flex-col leading-tight">
                  <span className="text-[15px] font-semibold text-[#111827]">
                    {fullName}
                  </span>
                  <span className="text-[13px] text-gray-500 ">{email}</span>
                </div>
              </Space>
            </div>

            {/* MENU */}
            <Menu
              mode="inline"
              selectedKeys={[selectedKey]}
              items={menuItems}
              onClick={({ key }) => setSelectedKey(key)}
              className="!bg-transparent !border-0 text-[15px]"
              style={{
                padding: "16px 0",
                fontWeight: 500,
              }}
            />
          </div>

          {/* LOGOUT BUTTON */}
          <div className="px-10 pb-20 border-t border-[#f2f2f2]">
            <Button
              type="text"
              icon={<LogoutOutlined />}
              danger
              className="w-full justify-start !px-3 !py-2 font-medium hover:!bg-[#fee2e2] hover:!text-[#b91c1c] transition-all"
              onClick={handleLogout}
            >
              Log out
            </Button>
          </div>
        </Sider>

        {/* ✅ CONTENT CARD */}
        <Content
          style={{
            flex: 1,
            background: "#ffffff",
            borderRadius: "28px",
            border: "1px solid #e5e7eb",
            boxShadow: "0 25px 55px rgba(0,0,0,0.05)",
            padding: "56px 64px",
            overflowY: "auto",
            transition: "all 0.3s ease",
          }}
        >
          <Space direction="vertical" size={40} className="w-full">
            {selectedKey !== "profile" && (
              <div>
                <Title
                  level={3}
                  className="!mb-1 !text-[#111827] font-semibold tracking-tight"
                >
                  Welcome back, {fullName.split(" ")[0] || fullName} 👋
                </Title>
              </div>
            )}

            <div className="min-h-[360px] transition-all duration-300">
              {selectedKey === "booking" && <Charge />}

              {selectedKey === "history" && (
                <Space direction="vertical" size={12}>
                  <Title level={4} className="!mb-0 !text-[#111827]">
                    History
                  </Title>
                  <Text type="secondary" className="text-[#6b7280]">
                    Review your past charging sessions.
                  </Text>
                </Space>
              )}

              {selectedKey === "wallet" && (
                <Space direction="vertical" size={12}>
                  <Title level={4} className="!mb-0 !text-[#111827]">
                    Wallet
                  </Title>
                  <Text type="secondary" className="text-[#6b7280]">
                    Check your balance and payment transactions.
                  </Text>
                </Space>
              )}

              {selectedKey === "profile" && (
                <Space direction="vertical" size={20}>
                  <div>
                    <Title level={4} className="!mb-1 !text-[#111827]">
                      My Profile
                    </Title>
                  </div>

                  <Row gutter={[24, 20]}>
                    <Col xs={24} md={12}>
                      <Card
                        className="bg-[#f9fafb] hover:bg-[#f3f4f6] transition-all"
                        bordered={false}
                      >
                        <Space direction="vertical" size={8}>
                          <Text className="uppercase text-[12px] text-gray-500">
                            Name
                          </Text>
                          <Text className="text-base font-semibold text-[#111827]">
                            {fullName}
                          </Text>
                        </Space>
                      </Card>
                    </Col>

                    <Col xs={24} md={12}>
                      <Card
                        className="bg-[#f9fafb] hover:bg-[#f3f4f6] transition-all"
                        bordered={false}
                      >
                        <Space direction="vertical" size={8}>
                          <Text className="uppercase text-[12px] text-gray-500">
                            Email
                          </Text>
                          <Text className="text-base font-semibold text-[#111827]">
                            {email}
                          </Text>
                        </Space>
                      </Card>
                    </Col>

                    <Col xs={24} md={12}>
                      <Card
                        className="bg-[#f9fafb] hover:bg-[#f3f4f6] transition-all"
                        bordered={false}
                      >
                        <Space direction="vertical" size={8}>
                          <Text className="uppercase text-[12px] text-gray-500">
                            Role
                          </Text>
                          <Text className="text-base font-semibold text-[#111827]">
                            {userInfo.role || "EV_DRIVER"}
                          </Text>
                        </Space>
                      </Card>
                    </Col>
                  </Row>
                </Space>
              )}
            </div>
          </Space>
          {/* <Outlet /> */}
        </Content>
      </Layout>
    </Layout>
  );
}
