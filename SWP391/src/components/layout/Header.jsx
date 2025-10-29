import React from "react";
import { Layout, Menu, Typography } from "antd";
import { Link, useLocation } from "react-router-dom";

const { Header: AntHeader } = Layout;

const navigationItems = [
  { key: "/", label: "Home" },
  { key: "/price", label: "Pricing" },
  { key: "/booking", label: "Booking" },
  { key: "/history", label: "History" },
  { key: "/login", label: "Login" },
];

const AppHeader = () => {
  const location = useLocation();
  const activeKey =
    navigationItems
      .filter((item) => item.key !== "/")
      .find((item) => location.pathname.startsWith(item.key))?.key || "/";

  return (
    <AntHeader className="!bg-[#050B0A] !h-auto px-6 py-4 lg:px-12">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <Link to="/" className="flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-full bg-[#0C1013] text-[#34C759] shadow-lg shadow-[#34C759]/20">
            <span className="text-xl font-semibold">EV</span>
          </div>
          <Typography.Title level={4} className="!m-0 text-white">
            EVCharge
          </Typography.Title>
        </Link>

        <div className="flex flex-1 justify-start md:justify-end">
          <Menu
            mode="horizontal"
            selectedKeys={[activeKey]}
            items={navigationItems.map((item) => ({
              key: item.key,
              label: (
                <Link
                  to={item.key}
                  className="text-sm font-medium text-white transition-colors duration-200 hover:text-gray-300"
                >
                  {item.label}
                </Link>
              ),
            }))}
            className="min-w-full justify-start border-none bg-transparent text-white md:min-w-fit [&_.ant-menu-item]:!border-b-0 [&_.ant-menu-item]:!px-2 [&_.ant-menu-item]:lg:!px-4 [&_.ant-menu-item-selected]:bg-transparent [&_.ant-menu-item-selected_a]:text-[#34C759] [&_.ant-menu-item:hover]:!bg-[#1A1F1D]"
          />
        </div>
      </div>
    </AntHeader>
  );
};

export default AppHeader;
