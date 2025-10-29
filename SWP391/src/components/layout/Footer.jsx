import React from "react";
import { Layout, Typography } from "antd";

const { Footer: AntFooter } = Layout;

const Footer = () => {
  return (
    <AntFooter className="!bg-[#050B0A] border-t border-[#1F2A24] py-6 text-center">
      <Typography.Text className="text-sm md:text-base !text-[#E9FFEE]/80">
        © {new Date().getFullYear()} EVCharge. All rights reserved.
      </Typography.Text>
    </AntFooter>
  );
};

export default Footer;
