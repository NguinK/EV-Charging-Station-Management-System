import { LockOutlined, MailOutlined, UserOutlined } from "@ant-design/icons";
import { Button, Card, Form, Input, Typography } from "antd";
import { Link as RouterLink } from "react-router-dom";

const { Title, Text } = Typography;

const formItemClassName =
  "[&_.ant-form-item-label>label]:!font-medium [&_.ant-form-item-label>label]:!text-slate-600 [&_.ant-input]:!bg-white [&_.ant-input]:!border-slate-200 [&_.ant-input:hover]:!border-emerald-300 [&_.ant-input]:!rounded-xl [&_.ant-input]:!py-2.5 [&_.ant-input]:!text-slate-700 [&_.ant-input-password-icon]:!text-emerald-500";

function RegisterPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-emerald-50 via-sky-50 to-white px-4 py-16">
      <Card className="w-full max-w-2xl border-none shadow-xl">
        <div className="flex flex-col gap-8 bg-white px-8 py-10">
          <div className="space-y-2 text-left">
            <Title level={3} className="!text-emerald-500">
              Join EV Station
            </Title>
            <Title level={2} className="!mb-0 !text-slate-800">
              Create an account
            </Title>
            <Text className="text-sm leading-relaxed text-slate-500">
              Already have an account?
              <RouterLink
                to="/login"
                className="ml-1 font-semibold text-emerald-500 hover:text-emerald-600"
              >
                Sign in
              </RouterLink>
              .
            </Text>
          </div>

          <Form
            layout="vertical"
            size="large"
            className="space-y-3"
            requiredMark={false}
            onFinish={(values) => {
              // eslint-disable-next-line no-console
              console.log("Register", values);
            }}
          >
            <Form.Item
              name="fullName"
              label="Full name"
              className={formItemClassName}
              rules={[{ required: true, message: "Please enter your full name" }]}
            >
              <Input prefix={<UserOutlined className="text-emerald-500" />} placeholder="Enter your full name" autoComplete="name" />
            </Form.Item>

            <Form.Item
              name="email"
              label="Email"
              className={formItemClassName}
              rules={[{ required: true, message: "Please enter your email" }]}
            >
              <Input prefix={<MailOutlined className="text-emerald-500" />} placeholder="Enter your email" autoComplete="email" />
            </Form.Item>

            <Form.Item
              name="password"
              label="Password"
              className={formItemClassName}
              rules={[{ required: true, message: "Please create a password" }]}
            >
              <Input.Password
                prefix={<LockOutlined className="text-emerald-500" />}
                placeholder="Create a password"
                autoComplete="new-password"
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
                    return Promise.reject(new Error("The passwords do not match"));
                  },
                }),
              ]}
            >
              <Input.Password
                prefix={<LockOutlined className="text-emerald-500" />}
                placeholder="Confirm your password"
                autoComplete="new-password"
              />
            </Form.Item>

            <Button
              type="primary"
              htmlType="submit"
              className="mt-2 h-12 w-full rounded-full bg-emerald-500 text-base font-semibold shadow-lg shadow-emerald-200 transition hover:bg-emerald-400"
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
