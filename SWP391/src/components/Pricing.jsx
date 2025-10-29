import React from "react";
import { Layout, Row, Col, Card, Typography, Button, List } from "antd";
import { CheckCircleFilled } from "@ant-design/icons";
import EVHeader from "./layout/Header";
import { EVFooter } from "./layout/Footer";
import { useNavigate } from "react-router-dom";

const { Content } = Layout;

const plans = [
  {
    title: "ONE TIME",
    price: "9.000 VND/KWH",
    description: null,
    features: ["All station", "Booking Free"],
    buttonLabel: "Get Started",
  },
  {
    title: "SUBSCRIPTION",
    price: "1.000.000 VND",
    description: "Per Year",
    features: [
      "All station",
      "Booking Free",
      "4200 VND / KWH",
      "Prioritize fast charging types",
      "No waiting",
    ],
    buttonLabel: "Get Started",
  },
];

export default function PricePage() {
  const navigate = useNavigate();

  const handleGetStarted = () => {
    const token = localStorage.getItem("token");

    if (!token) {
      navigate("/login");
    }
  };

  return (
    <>
      <EVHeader />
      <Layout className="min-h-screen bg-[#dde1e1]">
        <Content className="flex flex-col items-center px-6 pb-24 pt-[180px]">
          <Typography.Title
            level={2}
            className="mb-12 text-3xl font-extrabold tracking-normal !text-[#26bf38] md:text-4xl"
          >
            PRICE
          </Typography.Title>

          <Row gutter={[32, 32]} justify="center" className="w-full max-w-5xl">
            {plans.map((plan) => (
              <Col xs={24} md={12} key={plan.title}>
                <Card className="h-full rounded-2xl border border-[#482b98] bg-[#424b57]/90 shadow-[0_24px_60px_rgba(85, 171, 157, 0.55)] backdrop-blur-sm [&_.ant-card-body]:flex [&_.ant-card-body]:h-full [&_.ant-card-body]:flex-col [&_.ant-card-body]:gap-6 [&_.ant-card-body]:p-8 [&_.ant-card-body]:text-white">
                  <header className="flex flex-col items-center text-center">
                    <Typography.Title
                      level={4}
                      className="mb-2 text-base font-extrabold uppercase tracking-normal !text-[#7cf69a]"
                    >
                      {plan.title}
                    </Typography.Title>
                    <Typography.Title
                      level={2}
                      className={`text-3xl font-white -!text-white md:text-4xl ${
                        plan.description ? "mb-2" : "mb-8"
                      }`}
                    >
                      {plan.price}
                    </Typography.Title>
                    {plan.description && (
                      <Typography.Paragraph className="mb-8 text-sm uppercase tracking-normal !text-[#8f9a99]">
                        {plan.description}
                      </Typography.Paragraph>
                    )}
                  </header>

                  <div className="flex-1">
                    <List
                      dataSource={plan.features}
                      className="mt-2"
                      renderItem={(item) => (
                        <List.Item className="border-none px-0 py-3">
                          <List.Item.Meta
                            avatar={
                              <CheckCircleFilled className="text-lg text-[#49eb85]" />
                            }
                            title={
                              <Typography.Text className="text-lg font-semibold !text-white">
                                {item}
                              </Typography.Text>
                            }
                          />
                        </List.Item>
                      )}
                    />
                  </div>

                  <Button
                    type="primary"
                    htmlType="submit"
                    className="h-12 w-full rounded-full border-none bg-gradient-to-r from-[#2cd06d] to-[#49eb85] text-base font-semibold text-[#fcfcfc] shadow-[0_15px_30px_rgba(18,70,38,0.35)] transition hover:from-[#34c759] hover:to-[#45e47d]"
                    onClick={handleGetStarted}
                  >
                    {plan.buttonLabel}
                  </Button>
                </Card>
              </Col>
            ))}
          </Row>
        </Content>
        <EVFooter />
      </Layout>
    </>
  );
}
