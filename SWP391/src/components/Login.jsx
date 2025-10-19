import { FacebookFilled, GoogleOutlined } from "@ant-design/icons";
import {
  Button,
  Card,
  Checkbox,
  Divider,
  Form,
  Input,
  Space,
  Typography,
} from "antd";
import { Link as RouterLink } from "react-router-dom";

const { Title, Text } = Typography;

const formItemClassName =
  "[&_.ant-form-item-label>label]:!font-medium [&_.ant-form-item-label>label]:!text-slate-600 [&_.ant-input]:!bg-white [&_.ant-input]:!border-slate-200 [&_.ant-input:hover]:!border-emerald-300 [&_.ant-input]:!rounded-xl [&_.ant-input]:!py-2.5 [&_.ant-input]:!text-slate-700 [&_.ant-input-password-icon]:!text-emerald-500";

function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-blue-50 via-emerald-50 to-white px-4 py-16">
      <Card className="w-full max-w-4xl overflow-hidden border-none shadow-xl">
        <div className="grid gap-0 md:grid-cols-2">
          <div className="flex flex-col justify-between bg-white px-8 py-10">
            <div className="space-y-2 text-left">
              <Title level={3} className="!text-emerald-500">
                EV Station
              </Title>
              <Title level={2} className="!mb-0 !text-slate-800">
                Welcome back 👋
              </Title>
              <Text className="text-sm leading-relaxed text-slate-500">
                If you don't have an account yet, you can
                <RouterLink
                  to="/register"
                  className="ml-1 font-semibold text-emerald-500 hover:text-emerald-600"
                >
                  create one here
                </RouterLink>
                .
              </Text>
            </div>

            <Form
              layout="vertical"
              size="large"
              className="mt-10 space-y-3"
              requiredMark={false}
              onFinish={(values) => {
                // eslint-disable-next-line no-console
                console.log("Login", values);
              }}
            >
              <Form.Item
                name="email"
                label="Email"
                className={formItemClassName}
                rules={[{ required: true, message: "Please enter your email" }]}
              >
                <Input placeholder="Enter your email address" autoComplete="email" />
              </Form.Item>

              <Form.Item
                name="password"
                label="Password"
                className={formItemClassName}
                rules={[{ required: true, message: "Please enter your password" }]}
              >
                <Input.Password placeholder="Enter your password" autoComplete="current-password" />
              </Form.Item>

              <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <Form.Item name="remember" valuePropName="checked" className="!mb-0">
                  <Checkbox>Remember me</Checkbox>
                </Form.Item>
                <Button type="link" className="!px-0 !font-medium !text-emerald-500 hover:!text-emerald-600">
                  Forgot password?
                </Button>
              </div>

              <Button
                type="primary"
                htmlType="submit"
                className="h-12 w-full rounded-full bg-emerald-500 text-base font-semibold shadow-lg shadow-emerald-200 transition hover:bg-emerald-400"
              >
                Sign in
              </Button>
            </Form>

            <div className="mt-12">
              <Divider plain className="!text-slate-400">
                or continue with
              </Divider>
              <Space size="large" className="mt-4 flex justify-center">
                <Button
                  shape="circle"
                  icon={<FacebookFilled />}
                  className="h-12 w-12 !border-slate-200 !bg-white !text-blue-500 hover:!border-blue-200 hover:!text-blue-600"
                />
                <Button
                  shape="circle"
                  className="h-12 w-12 !border-slate-200 !bg-white !text-slate-600 hover:!border-slate-300"
                >
                  
                </Button>
                <Button
                  shape="circle"
                  icon={<GoogleOutlined />}
                  className="h-12 w-12 !border-slate-200 !bg-white !text-rose-500 hover:!border-rose-200"
                />
              </Space>
            </div>
          </div>

          <div className="relative hidden min-h-full flex-col justify-between bg-gradient-to-br from-emerald-500 via-emerald-400 to-sky-400 p-10 text-white md:flex">
            <div>
              <Title level={2} className="!text-white">
                Charge smarter, not harder
              </Title>
              <Text className="block text-sm text-emerald-50/90">
                Manage your EV charging sessions, monitor usage, and keep your stations running effortlessly.
              </Text>
            </div>
            <div className="rounded-3xl border border-white/40 bg-white/20 p-6 text-sm leading-relaxed shadow-2xl backdrop-blur">
              “EV Station helps us keep charging stations available for our community without the usual headaches.”
              <div className="mt-4 font-semibold">— Green Mobility Team</div>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
}

export default LoginPage;
