import React from "react";
import { Layout, Typography } from "antd";

const { Footer } = Layout;

export function EVFooter() {
  return (
    <Footer className="!bg-[#050B0A] py-6 text-center">
      <Typography.Text className="!text-[#E9FFEE] text-sm">
        © {new Date().getFullYear()} EVCharge. All rights reserved.
      </Typography.Text>
    </Footer>
  );
}

export default EVFooter;
