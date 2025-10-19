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
  "[&_.ant-form-item-label>label]:!text-[#9ab6a4] \
  [&_.ant-input]:!bg-[#0c1013] \
  [&_.ant-input]:!border-[#1f2a24] \
  [&_.ant-input]:!text-[#e9ffee] \
  [&_.ant-input]:!placeholder:text-[#5a6b63] \
  [&_.ant-input-affix-wrapper]:!bg-[#0c1013] \
  [&_.ant-input-affix-wrapper]:!border-[#1f2a24] \
  [&_.ant-input-affix-wrapper]:!text-[#e9ffee]";

function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-[#050b0a] px-4 text-white">
      <div className="absolute inset-0 -z-10">
        <div className="absolute right-[-140px] top-20 h-[420px] w-[420px] rounded-full bg-gradient-to-br from-[#2cd06d]/20 to-transparent blur-3xl" />
        <div className="absolute left-[-180px] bottom-10 h-[360px] w-[360px] rounded-full bg-gradient-to-tr from-[#1f8a4d]/20 to-transparent blur-3xl" />
      </div>

      <div className="mx-auto grid w-full max-w-6xl gap-8 md:grid-cols-[1fr,0.95fr]">
        <Card className="w-full max-w-md bg-[#101418]/95 !border-[#1c2320] !shadow-[0_20px_60px_rgba(8,19,15,0.55)] p-8 rounded-2xl"></Card>
        <Card className="bg-[#101418]/95 !border-[#1c2320] !shadow-[0_30px_80px_rgba(8,19,15,0.55)]">
          <div className="flex flex-col gap-6">
            <div className="flex items-center gap-3">
              {/* <img src="/ev_logo.png" alt="EV Station" className="h-6 w-auto" /> */}
              <Title level={3} className="text-3xl font-bold !text-[#4ef58b]">
                EV Station
              </Title>
            </div>

            <div className="space-y-2">
              <Title level={2} className="!mb-0 !text-[#7cf69a]">
                Sign in
              </Title>
              <Text className="text-sm  leading-relaxed text-[#8f9a99]">
                If you don’t have an account register
                <br />
                You can
                <RouterLink
                  to="/register"
                  className="ml- font-medium text-[#394d40] transition-colors hover:text-[#b9ffda] !text-[#7cf69a] "
                >
                  Register here!
                </RouterLink>
              </Text>
            </div>

            <Form
              layout="vertical"
              size="large"
              className="space-y-2"
              requiredMark={false}
              onFinish={(values) => {
                console.log("Login", values);
              }}
            >
              <Form.Item
                name="email"
                label="Email"
                className={formItemClassName}
                rules={[{ required: true, message: "Please enter your email" }]}
              >
                <Input placeholder="Enter your email address" />
              </Form.Item>

              <Form.Item
                name="password"
                label="Password"
                className={formItemClassName}
                rules={[
                  { required: true, message: "Please enter your password" },
                ]}
              >
                <Input.Password placeholder="Enter your password" />
              </Form.Item>

              <div className="flex items-center justify-between text-xs text-[#a0b5a9]">
                <Form.Item
                  name="remember"
                  valuePropName="checked"
                  className="!mb-0"
                >
                  <Checkbox className="text-[#a9bbb4]">Remember me</Checkbox>
                </Form.Item>
                <Button
                  type="link"
                  className="!p-0 !text-[#8fffba] hover:!text-[#b9ffda]"
                >
                  Forgot password?
                </Button>
              </div>

              <Button
                type="primary"
                htmlType="submit"
                className="h-12 w-full rounded-full border-none bg-gradient-to-r from-[#2cd06d] to-[#49eb85] text-base font-semibold text-[#042410] shadow-[0_15px_30px_rgba(18,70,38,0.35)] transition hover:from-[#34c759] hover:to-[#45e47d]"
              >
                Login
              </Button>
            </Form>

            <Divider plain className="!text-[#98ad9f]">
              or continue with
            </Divider>

            <Space size="middle" className="justify-center">
              <Button
                shape="circle"
                icon={<FacebookFilled />}
                className="h-12 w-12 !bg-[#0c1113] !text-[#1877f2] hover:!bg-[#172026]"
              />
              <Button
                shape="circle"
                icon={<GoogleOutlined />}
                className="h-12 w-12 !bg-[#0c1113] !text-[#34a853] hover:!bg-[#172026]"
              />
            </Space>
          </div>
        </Card>
      </div>
    </div>
  );
}

export default LoginPage;
