import React from "react";
import { Layout, Typography, Card, Button } from "antd";
import { Link } from "react-router-dom";
import Header from "./layout/Header";
import Footer from "./layout/Footer";

const { Content } = Layout;

const connectorTypes = [
  "CCS2",
  "Type 2 (Mennekes)",
  "CHAdeMO (optional legacy support)",
  "AC Slow / Fast Charging",
  "DC Fast Charging (up to 150kW)",
];

const HomePage = () => {
  return (
    <Layout className="min-h-screen bg-[#050B0A] text-white">
      <Header />
      <Content className="flex justify-center px-6 py-16 lg:py-24">
        <div className="flex w-full max-w-6xl flex-col-reverse items-center gap-12 lg:flex-row lg:items-stretch">
          <div className="w-full lg:w-1/2">
            <Card
              bordered
              className="relative h-full overflow-hidden border-[#1F2A24] bg-[#0C1013] !rounded-[32px] p-8 sm:p-12"
            >
              <div className="pointer-events-none absolute inset-6 rounded-[28px] border border-[#1F2A24]/70"></div>
              <img
                src="https://images.unsplash.com/photo-1617814074846-1f3c8c12c325?auto=format&fit=crop&w=1200&q=80"
                alt="Touch to charge EV"
                className="relative z-[1] mx-auto w-full max-w-md animate-charge drop-shadow-[0_0_35px_rgba(52,199,89,0.35)]"
              />
            </Card>
          </div>

          <div className="flex w-full flex-col justify-center space-y-8 lg:w-1/2">
            <div className="space-y-5">
              <Typography.Title
                level={1}
                className="!m-0 text-4xl font-bold tracking-tight text-white md:text-5xl"
              >
                Charging Your EV in One Simple Tap
              </Typography.Title>
              <Typography.Paragraph className="!m-0 text-lg leading-relaxed text-[#E9FFEE]/80 md:text-xl">
                Experience seamless European-standard EV charging with our advanced platform.
              </Typography.Paragraph>
            </div>

            <Card className="space-y-5 border border-[#1F2A24] bg-[#0C1013] !rounded-3xl p-6">
              <Typography.Title
                level={5}
                className="!m-0 text-xs font-semibold uppercase tracking-[0.35em] text-[#E9FFEE]/70"
              >
                Supported Connector Types
              </Typography.Title>
              <ul className="space-y-3">
                {connectorTypes.map((type) => (
                  <li key={type} className="flex items-center gap-3">
                    <span className="inline-flex h-2.5 w-2.5 rounded-full bg-[#34C759]"></span>
                    <Typography.Text className="!text-base !text-[#E9FFEE]">
                      {type}
                    </Typography.Text>
                  </li>
                ))}
              </ul>
            </Card>

            <div className="flex flex-wrap items-center gap-4">
              <Button
                type="primary"
                size="large"
                className="!bg-[#34C759] !border-none !text-black hover:!bg-[#2eae4c]"
              >
                <Link to="/booking" className="font-semibold">
                  Book a Charging Session
                </Link>
              </Button>
              <Button
                size="large"
                ghost
                className="!border-[#1F2A24] !text-[#E9FFEE] hover:!border-[#34C759] hover:!text-[#34C759]"
              >
                <Link to="/price" className="font-semibold">
                  View Pricing
                </Link>
              </Button>
            </div>
          </div>
        </div>
      </Content>
      <Footer />
    </Layout>
  );
};

export default HomePage;
