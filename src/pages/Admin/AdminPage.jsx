import React, { useState } from "react";
import {
  DesktopOutlined,
  PieChartOutlined,
  ToolOutlined,
  UserOutlined,
  WalletOutlined,
} from "@ant-design/icons";
import { Breadcrumb, ConfigProvider, Layout, Menu, theme } from "antd";
import HistoryPage from "./HistoryPage";
import BookingPage from "../BookingPage";
import EditProfile from "./EditAccount";

const { Header, Content, Footer, Sider } = Layout;

function getItem(label, key, icon, children) {
  return { key, icon, children, label };
}

const items = [
  getItem("Station", "booking", <PieChartOutlined />),
  getItem("History", "history", <DesktopOutlined />),
  getItem("Account", "account", <UserOutlined />),
  getItem("Wallet", "Wallet", <WalletOutlined />),
  

];

const Admin = () => {
  const [collapsed, setCollapsed] = useState(false);
  const [selectedKey, setSelectedKey] = useState("booking");

  const {
    token: { colorBgContainer },
  } = theme.useToken();
  const renderContent = () => {
    switch (selectedKey) {
      case "booking":
        return <BookingPage />;
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
          width={300} // ✅ sidebar rộng hơn (default 200)
          // collapsedWidth={80} // ✅ khi collapse vẫn đẹp
          collapsible
          collapsed={collapsed}
          onCollapse={(value) => setCollapsed(value)}
        >
          <div className="demo-logo-vertical" />
          <Menu
            theme="dark"
            defaultSelectedKeys={["booking"]}
            mode="inline"
            items={items}
            onClick={({ key }) => setSelectedKey(key)}
          />
        </Sider>

        {/* MAIN LAYOUT */}
        <Layout>
          <Header style={{ padding: 0, background: colorBgContainer }} />

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
