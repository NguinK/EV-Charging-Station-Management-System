import React from "react";
import { Layout, Typography, Card } from "antd";
import EVHeader from "./layout/Header";
import EVFooter from "./layout/Footer";

const { Content } = Layout;
const { Title, Paragraph, Text } = Typography;

const connectorTypes = [
  "CCS2",
  "Type 2 (Mennekes)",
  "CHAdeMO (optional legacy support)",
  "AC Slow / Fast Charging",
  "DC Fast Charging (up to 150kW)",
];

export default function HomePage() {
  return (
    <Layout className="min-h-screen bg-[#050B0A] text-[#E9FFEE]">
      <EVHeader />

      <Content className="flex flex-col">
        <section className="mx-auto flex w-full max-w-6xl flex-col-reverse items-center gap-12 px-6 py-16 lg:flex-row lg:px-12 xl:px-16">
          <div className="w-full space-y-6 lg:w-1/2">
            <Title level={1} className="!m-0 !text-4xl md:!text-5xl !leading-tight !text-[#E9FFEE]">
              Charging Your EV in One Simple Tap
            </Title>
            <Paragraph className="!m-0 !text-base !text-[#D4E9DC] md:!text-lg">
              Experience seamless European-standard EV charging with our advanced platform.
            </Paragraph>

            <Card className="!bg-[#0C1013] !border-[#1F2A24] !shadow-none !text-[#E9FFEE]" bodyStyle={{ padding: "1.75rem" }}>
              <Title level={4} className="!mt-0 !text-[#E9FFEE] !mb-4">
                Supported connector types
              </Title>
              <Paragraph className="!m-0 !text-sm !text-[#CFE4D6]">
                Covering every European charging standard so your EV is always ready for the next journey.
              </Paragraph>
              <ul className="mt-5 space-y-3">
                {connectorTypes.map((type) => (
                  <li key={type} className="flex items-center gap-3 text-sm text-[#E9FFEE] md:text-base">
                    <span className="h-2 w-2 rounded-full bg-emerald-400 shadow-[0_0_10px_rgba(16,185,129,0.6)]" />
                    <Text className="!text-[#E9FFEE]">{type}</Text>
                  </li>
                ))}
              </ul>
            </Card>
          </div>

          <div className="flex w-full items-center justify-center lg:w-1/2">
            <div className="relative flex aspect-square w-full max-w-md items-center justify-center rounded-3xl border border-[#1F2A24] bg-[#0C1013] p-6 shadow-[0_25px_60px_rgba(3,7,6,0.45)]">
              <div className="absolute inset-4 rounded-2xl bg-gradient-to-br from-emerald-500/10 via-transparent to-transparent blur-2xl" aria-hidden="true" />
              <img
                src="https://images.unsplash.com/photo-1617819950380-1a56d53295f8?auto=format&fit=crop&w=900&q=80"
                alt="Touch to charge EV"
                className="relative z-10 w-full max-w-sm animate-pulse drop-shadow-[0_0_30px_rgba(16,185,129,0.4)]"
                loading="lazy"
              />
              <div className="absolute inset-6 rounded-2xl border border-emerald-500/20" aria-hidden="true" />
            </div>
          </div>
        </section>
      </Content>

      <EVFooter />
    </Layout>
  );
}
