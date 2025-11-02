import React, { useState } from "react";
import {
  DesktopOutlined,
  PieChartOutlined,
  ToolOutlined,
  UserOutlined,
  WalletOutlined,
} from "@ant-design/icons";
import { Breadcrumb, ConfigProvider, Layout, Menu, } from "antd";
import HistoryPage from "./HistoryPage";
import EditProfile from "./EditAccount";
import ChargingStation from "./ChargingStation";

const { Header, Content, Footer, Sider } = Layout;

function getItem(label, key, icon, children) {
  return { key, icon, children, label };
}

const items = [
  getItem("Station", "station", <PieChartOutlined />),
  getItem("History", "history", <DesktopOutlined />),
  getItem("Account", "account", <UserOutlined />),
  getItem("Wallet", "Wallet", <WalletOutlined />),
  

];

const Admin = () => {
  const [collapsed, setCollapsed] = useState(false);
  const [selectedKey, setSelectedKey] = useState("station");

   const renderContent = () => {
    switch (selectedKey) {
      case "station":
        return <ChargingStation />;
      case "history":
        return <HistoryPage />;
      case "account":
        return <EditProfile />;
      default:
        return (
          <div className="text-gray-500">
            Chọn một mục từ menu để hiển thị nội dung.
          </div>
        );
    }
  };

  return (
    <ConfigProvider
      theme={{
        components: {
          Menu: {
            itemHeight: 70, // ✅ tăng chiều cao item sidebar
            fontSize: 20, // ✅ chữ to
            iconSize: 20, // ✅ icon to
            itemPaddingInline:60
          },
        },
      }}
    >
      <Layout style={{ minHeight: "100vh" }}>
        {/* SIDEBAR */}
        <Sider
          width={300} 
          collapsible
          collapsed={collapsed}
          onCollapse={(value) => setCollapsed(value)}
        >
          <div className="demo-logo-vertical" />
          <Menu
            theme="dark"
            defaultSelectedKeys={["station"]}
            mode="inline"
            items={items}
            onClick={({ key }) => setSelectedKey(key)}
          />
        </Sider>

        {/* MAIN LAYOUT */}
        <Layout>
         

          <Content style={{ margin: "0 16px" }}>
            <Breadcrumb style={{ margin: "16px 0" }}>
              <Breadcrumb.Item>Dashboard</Breadcrumb.Item>
              <Breadcrumb.Item>{selectedKey}</Breadcrumb.Item>
            </Breadcrumb>

            <div style={{ padding: 0 }}>{renderContent()}</div>
          </Content>

          <Footer style={{ textAlign: "center" }}>
            EV Station Dashboard ©{new Date().getFullYear()}
          </Footer>
        </Layout>
      </Layout>
    </ConfigProvider>
  );
};
export default Admin;
