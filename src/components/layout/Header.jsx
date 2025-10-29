import React from "react";
import { Layout, Button } from "antd";
import { Link } from "react-router-dom";
import { LoginOutlined } from "@ant-design/icons";

const { Header } = Layout;

const EVHeader = () => {
  return (
    <Header className="flex justify-between items-center bg-[#19221d] px-8 h-14 border-b border-gray-700 shadow-sm">
      {/* Logo = nút Home */}
      <Link
        to="/"
        className="flex items-center gap-2 hover:opacity-80 transition-all duration-200"
      >
        <div className="w-9 h-3 flex items-center justify-center overflow-hidden rounded-full bg-transparent">
          <img
            src="/ev_logo.png"
            alt="EV Station"
            style={{ width: "120%", height: "100%" }}
          />
        </div>
      </Link>
      {/* Nút Login */}
      <Link to="/login">
        <Button
          type="default"
          icon={<LoginOutlined />}
          className="h-12 w-full rounded-full border-none bg-gradient-to-r from-[#2729b8] to-[#49eb85] text-base font-semibold text-[#042410] shadow-[0_15px_30px_rgba(18,70,38,0.35)] transition hover:from-[#34c759] hover:to-[#45e47d]"
        >
          Login
        </Button>
      </Link>
    </Header>
  );
};

export default EVHeader;
