import React, { useState } from "react";
import {
  DesktopOutlined,
  PieChartOutlined,
  UserOutlined,
  WalletOutlined,
  LogoutOutlined,
} from "@ant-design/icons";
import { Layout, Avatar, message } from "antd";
import { useNavigate } from "react-router-dom";
import Charge from "./Charge";

const { Sider, Content } = Layout;

export default function UserPage() {
  const navigate = useNavigate();
  const [selectedKey, setSelectedKey] = useState("booking");

  const userInfo = JSON.parse(localStorage.getItem("userInfo")) || {};
  const fullName = userInfo.fullName || "User";
  const email = userInfo.email || "example@gmail.com";

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");
    message.success("Logged out!");
    navigate("/");
  };

  return (
    <Layout className="min-h-screen bg-[#f3f5f7]">

      {/* ✅ Sidebar UI đẹp & sạch */}
      <Sider
        width={260}
        style={{
          background: "#f7f7f7",
          borderRight: "1px solid #e1e1e1",
          paddingTop: 20,
          display: "flex",
          flexDirection: "column",
        }}
      >

        {/* ✅ Profile Area */}
        <div
          onClick={() => setSelectedKey("profile")}
          className="cursor-pointer mx-4 mb-8 rounded-xl px-4 py-4 hover:bg-[#ececec] transition flex items-center gap-3"
        >
          <Avatar
            size={55}
            style={{
              backgroundColor: "#4ef58b",
              fontWeight: "bold",
              color: "#003300",
            }}
          >
            {fullName.charAt(0)}
          </Avatar>

          <div className="leading-tight">
            <div className="text-[18px] font-semibold text-[#222]">{fullName}</div>
            <div className="text-[13px] text-gray-500">{email}</div>
          </div>
        </div>

        {/* ✅ Menu Section (gọn – spacing đẹp) */}
        <div className="flex flex-col gap-4 px-6 text-[16px] font-medium text-[#333]">

          {/* Booking */}
          <div
            className={`flex items-center gap-3 cursor-pointer py-2 rounded-md transition ${
              selectedKey === "booking"
                ? "text-[#1fb86b]"
                : "hover:text-[#1fb86b]"
            }`}
            onClick={() => setSelectedKey("booking")}
          >
            <PieChartOutlined />
            Booking
          </div>

          {/* History */}
          <div
            className={`flex items-center gap-3 cursor-pointer py-2 rounded-md transition ${
              selectedKey === "history"
                ? "text-[#1fb86b]"
                : "hover:text-[#1fb86b]"
            }`}
            onClick={() => setSelectedKey("history")}
          >
            <DesktopOutlined />
            History
          </div>

          {/* Wallet */}
          <div
            className={`flex items-center gap-3 cursor-pointer py-2 rounded-md transition ${
              selectedKey === "wallet"
                ? "text-[#1fb86b]"
                : "hover:text-[#1fb86b]"
            }`}
            onClick={() => setSelectedKey("wallet")}
          >
            <WalletOutlined />
            Wallet
          </div>
        </div>

        {/* ✅ Logout luôn ở đáy */}
        <div
          onClick={handleLogout}
          className="
            mt-auto mx-4 mb-6 px-4 py-3 flex items-center gap-2 
            cursor-pointer text-red-500 font-medium rounded-lg
            hover:bg-red-50 transition
          "
        >
          <LogoutOutlined />
          Log Out
        </div>
      </Sider>

      {/* ✅ MAIN CONTENT (đẹp – sáng – premium) */}
      <Content className="p-8 bg-[#f3f5f7]">
        <div
          className="
            bg-white border border-[#e1e1e1] rounded-2xl p-8 min-h-[75vh]
            shadow-[0_4px_20px_rgba(0,0,0,0.05)]
          "
        >
          {selectedKey === "booking" && <Charge />}

          {selectedKey === "history" && (
            <div className="text-2xl font-semibold text-[#222]">History</div>
          )}

          {selectedKey === "wallet" && (
            <div className="text-2xl font-semibold text-[#222]">Wallet</div>
          )}

          {selectedKey === "profile" && (
            <div>
              <div className="text-2xl font-semibold text-[#222]">My Profile</div>
              <div className="mt-4 text-lg text-gray-600 leading-relaxed">
                <p><b>Name:</b> {fullName}</p>
                <p><b>Email:</b> {email}</p>
                <p><b>Role:</b> {userInfo.role || "User"}</p>
              </div>
            </div>
          )}
        </div>
      </Content>

    </Layout>
  );
}
