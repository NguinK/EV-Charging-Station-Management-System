import React from "react";
import { Layout, Menu } from "antd";
import { Link, useLocation } from "react-router-dom";

const { Header } = Layout;

const navItems = [
  { key: "/", label: "Home", path: "/" },
  { key: "/price", label: "Pricing", path: "/price" },
  { key: "/booking", label: "Booking", path: "/booking" },
  { key: "/history", label: "History", path: "/history" },
];

export default function EVHeader() {
  const location = useLocation();

  const activeKey =
    navItems.find((item) =>
      item.key === "/"
        ? location.pathname === "/"
        : location.pathname.startsWith(item.key)
    )?.key || "/";

  return (
    <Header className="flex items-center justify-between !bg-[#050B0A] !px-4 md:!px-12 !h-20 !leading-none">
      <Link
        to="/"
        className="text-2xl font-semibold tracking-wide text-[#E9FFEE] no-underline transition-colors duration-200 hover:text-gray-300 focus-visible:outline-none"
      >
        EVCharge
      </Link>

      <div className="flex items-center gap-6">
        <Menu
          mode="horizontal"
          selectedKeys={[activeKey]}
          items={navItems.map((item) => ({
            key: item.key,
            label: (
              <Link
                to={item.path}
                className="text-sm font-medium text-[#E9FFEE] no-underline transition-colors duration-200 hover:text-gray-300 focus-visible:outline-none"
              >
                {item.label}
              </Link>
            ),
            style: {
              background: "transparent",
            },
          }))}
          className="!bg-transparent !border-0 !border-b-0 !text-[#E9FFEE] [&_.ant-menu-item]:!bg-transparent [&_.ant-menu-item]:!px-0 md:[&_.ant-menu-item]:!px-4 [&_.ant-menu-item]:!text-[#E9FFEE] [&_.ant-menu-item]:focus-visible:!outline-none [&_.ant-menu-item-selected]:!bg-transparent [&_.ant-menu-item-selected]:!text-white [&_.ant-menu-item:hover]:!bg-transparent"
        />

        <Link
          to="/login"
          className="rounded-full bg-emerald-500 px-5 py-2 text-sm font-semibold text-[#050B0A] transition-transform duration-200 hover:scale-105 focus-visible:outline-none"
        >
          Login
        </Link>
      </div>
    </Header>
  );
}
