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
  message,
  Alert,
} from "antd";

import { Link as RouterLink, useNavigate } from "react-router-dom";
import authAPI from "../api/authAPI";
import { useState } from "react";

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
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (values) => {
    try {
      // ✅ Gửi request đúng định dạng backend yêu cầu
      const res = await authAPI.postLoginUser({
        email: values.email,
        password: values.password,
      });

      console.log("✅ Login success:", res.data);

      // ✅ Lấy token từ response
      const token = res?.data?.token;
      const userInfo = {
        id: res?.data?.driverId, 
        driverId: res?.data?.driverId, 
        fullName: res?.data?.fullName,
        email: res?.data?.email,
        role: res?.data?.role,
      };

      // ✅ Lưu token + user info
      localStorage.setItem("token", token);
      localStorage.setItem("userInfo", JSON.stringify(userInfo));

      message.success("Login successful!");
      navigate("/user");
      if (userInfo.role === "ADMIN") {
        navigate("/admin");
      } else {
        navigate("/user");
      }
    } catch (error) {
      console.error("❌ Login error:", error.response?.data || error.message);

      if (error.response?.status === 400) {
        setErrorMessage("Sai email hoặc mật khẩu!");
      } else {
        setErrorMessage("Lỗi server hoặc kết nối thất bại!");
      }
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-[#798583] px-4 text-white">
      <div className="absolute inset-0 -z-10">
        <div className="absolute right-[-140px] top-20 h-[420px] w-[420px] rounded-full bg-gradient-to-br from-[#762cd0]/20 to-transparent blur-3xl" />
        <div className="absolute left-[-180px] bottom-10 h-[360px] w-[360px] rounded-full bg-gradient-to-tr from-[#1f8a4d]/20 to-transparent blur-3xl" />
      </div>

      <div className="mx-auto grid w-full max-w-6xl gap8 md:grid-cols-[1fr,0.95fr]">
        <Card className="bg-[#020203]/95 !border-[#254135] !shadow-[0_30px_80px_rgba(8,19,15,0.55)]">
          <div className="flex flex-col gap-6">
            <div className="text-center mb-2">
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
                  className="ml- font-bold  transition-colors hover:text-[#b9ffda] !text-[#7cf69a] "
                >
                  Register here!
                </RouterLink>
              </Text>
            </div>

            <Form layout="vertical" size="large" onFinish={handleLogin}>
              <Form.Item
                name="email"
                label="Email"
                className={formItemClassName}
                rules={[{ message: "Please enter your email" }]}
              >
                <Input placeholder="Enter your email address" />
              </Form.Item>

              <Form.Item
                name="password"
                label="Password"
                className={formItemClassName}
                rules={[{ message: "Please enter your password" }]}
              >
                <Input.Password placeholder="Enter your password" />
              </Form.Item>

              {errorMessage && (
                <Alert
                  message={errorMessage}
                  type="error"
                  showIcon
                  className="mb-3 bg-[#e0e0e1] border-[#3a4045]"
                />
              )}

              <div className="flex items-center justify-between text-xs text-[#a0b5a9]">
                <Form.Item
                  name="remember"
                  valuePropName="checked"
                  className="!mb-0"
                ></Form.Item>
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
                className="h-12 w-full rounded-full border-none bg-gradient-to-r from-[#2729b8] to-[#49eb85] text-base font-semibold text-[#042410] shadow-[0_15px_30px_rgba(18,70,38,0.35)] transition hover:from-[#34c759] hover:to-[#45e47d]"
              >
                Login
              </Button>
            </Form>

            <Divider plain className="!text-[#98ad9f]">
              or continue with
            </Divider>
          </div>
        </Card>
      </div>
    </div>
  );
}

export default LoginPage;
