import { LockOutlined, MailOutlined, UserOutlined } from "@ant-design/icons";
import { Button, Card, Form, Input, Typography } from "antd";
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
function RegisterPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-[#050b0a] px-4 text-white">
      <div className="absolute inset-0 -z-10">
        <div className="absolute left-[-120px] top-24 h-[420px] w-[420px] rounded-full bg-gradient-to-br from-[#2cd06d]/15 to-transparent blur-3xl" />
        <div className="absolute right-[-160px] bottom-10 h-[360px] w-[360px] rounded-full bg-gradient-to-tr from-[#1f8a4d]/20 to-transparent blur-3xl" />
      </div>

      <Card className="mx-auto w-full max-w-2xl bg-[#101418]/95 !border-[#1c2320] !shadow-[0_30px_80px_rgba(8,19,15,0.55)]">
        <div className="flex flex-col gap-6">
          <div className="flex items-center gap-3">
            {/* <img src="/ev_logo.png" alt="EV Station" className="h-6 w-auto" /> */}
            <Title level={3} className="!m-0 !text-[#4ef58b]">
              EV Station
            </Title>
          </div>

          <div className="space-y-2">
            <Title level={2} className="!mb-0 !text-[#7cf69a]">
              Create an account
            </Title>
            <Text className="text-sm leading-relaxed text-[#8f9a99]">
              Already have an account?
              <RouterLink
                to="/login"
                className="ml-1 font-medium text-[#8fffba] transition-colors hover:text-[#b9ffda] !text-[#7cf69a]"
              >
                Sign in
              </RouterLink>
            </Text>
          </div>

          <Form
            layout="vertical"
            size="large"
            className="space-y-2"
            requiredMark={false}
            onFinish={(values) => {
              console.log("Register", values);
            }}
          >
            <Form.Item
              name="fullName"
              label="Full name"
              className={formItemClassName}
              rules={[
                { required: true, message: "Please enter your full name" },
              ]}
            >
              <Input
                prefix={<UserOutlined className="text-[#bbc9c0]" />}
                placeholder="Enter your full name"
              />
            </Form.Item>

            <Form.Item
              name="email"
              label="Email"
              className={formItemClassName}
              rules={[{ required: true, message: "Please enter your email" }]}
            >
              <Input
                prefix={<MailOutlined className="text-[#bbc9c0]" />}
                placeholder="Enter your email"
              />
            </Form.Item>

            <Form.Item
              name="password"
              label="Password"
              className={formItemClassName}
              rules={[{ required: true, message: "Please create a password" }]}
            >
              <Input.Password
                prefix={<LockOutlined className="text-[#bbc9c0]" />}
                placeholder="Create a password"
              />
            </Form.Item>

            <Form.Item
              name="confirm"
              label="Confirm password"
              className={formItemClassName}
              dependencies={["password"]}
              rules={[
                { required: true, message: "Please confirm your password" },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue("password") === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(
                      new Error("The passwords do not match")
                    );
                  },
                }),
              ]}
            >
              <Input.Password
                prefix={<LockOutlined className="text-[#bbc9c0]" />}
                placeholder="Confirm your password"
              />
            </Form.Item>

            <Button
              type="primary"
              htmlType="submit"
              className="mt-4 h-12 w-full rounded-full border-none bg-gradient-to-r from-[#2cd06d] to-[#49eb85] text-base font-semibold text-[#042410] shadow-[0_15px_30px_rgba(18,70,38,0.35)] transition hover:from-[#34c759] hover:to-[#45e47d]"
            >
              Register
            </Button>
          </Form>
        </div>
      </Card>
    </div>
  );
}

export default RegisterPage;
