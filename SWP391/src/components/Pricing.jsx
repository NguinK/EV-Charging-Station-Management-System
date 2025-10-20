import React from "react";
import { Layout, Row, Col, Card, Typography, Button, List } from "antd";
import { CheckCircleFilled } from "@ant-design/icons";
import EVHeader from "./layout/Header";
import { EVFooter } from "./layout/Footer";

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
  return (
    <>
      <EVHeader />
      <Layout className="min-h-screen bg-neutral-950">
        <Content className="flex flex-col items-center px-6 pb-24 pt-28">
          <Typography.Title
            level={2}
            className="mb-12 text-3xl font-extrabold tracking-[0.2em] !text-zinc-100 md:text-4xl"
          >
            PRICE
          </Typography.Title>

          <Row gutter={[32, 32]} justify="center" className="w-full max-w-5xl">
            {plans.map((plan) => (
              <Col xs={24} md={12} key={plan.title}>
                <Card
                  bordered={false}
                  className="h-full rounded-2xl border border-neutral-800/80 bg-neutral-900/95 shadow-[0_16px_40px_rgba(15,15,15,0.65)] [&_.ant-card-body]:flex [&_.ant-card-body]:h-full [&_.ant-card-body]:flex-col [&_.ant-card-body]:gap-6 [&_.ant-card-body]:p-8 [&_.ant-card-body]:text-zinc-100"
                >
                  <header className="flex flex-col items-center text-center">
                    <Typography.Title
                      level={4}
                      className="mb-2 text-base font-extrabold uppercase tracking-[0.4em] !text-zinc-100"
                    >
                      {plan.title}
                    </Typography.Title>
                    <Typography.Title
                      level={2}
                      className={`text-3xl font-black !text-zinc-100 md:text-4xl ${
                        plan.description ? "mb-2" : "mb-8"
                      }`}
                    >
                      {plan.price}
                    </Typography.Title>
                    {plan.description && (
                      <Typography.Paragraph className="mb-8 text-sm uppercase tracking-[0.3em] !text-zinc-400">
                        {plan.description}
                      </Typography.Paragraph>
                    )}
                  </header>

                  <Button
                    type="primary"
                    size="large"
                    shape="round"
                    className="mx-auto mt-2 w-full max-w-xs !bg-emerald-500 !px-8 !py-5 text-sm font-semibold uppercase tracking-wider hover:!bg-emerald-400"
                  >
                    {plan.buttonLabel}
                  </Button>

                  <List
                    dataSource={plan.features}
                    className="mt-2"
                    renderItem={(item) => (
                      <List.Item className="border-none px-0 py-3">
                        <List.Item.Meta
                          avatar={<CheckCircleFilled className="text-lg text-emerald-500" />}
                          title={
                            <Typography.Text className="text-sm font-medium !text-zinc-100">
                              {item}
                            </Typography.Text>
                          }
                        />
                      </List.Item>
                    )}
                  />
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
