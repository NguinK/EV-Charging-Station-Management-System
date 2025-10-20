import React from "react";
import { Layout, Typography } from "antd";

const { Footer } = Layout;

export function EVFooter() {
  return (
    <Footer className="border-t border-neutral-900 bg-neutral-950 py-6 text-center">
      <Typography.Text className="text-sm font-medium !text-zinc-200">
        EVStation ©{new Date().getFullYear()}
      </Typography.Text>
    </Footer>
  );
}
