import {
  LockOutlined,
  MailOutlined,
  UserOutlined,
  PhoneOutlined,
  CarOutlined,
  IdcardOutlined,
  HomeOutlined,
  CalendarOutlined,
} from "@ant-design/icons";
import {
  Button,
  Card,
  Form,
  Input,
  Typography,
  message,
  DatePicker,
  Row,
  Col,
} from "antd";
import { useNavigate, Link as RouterLink } from "react-router-dom";
import authAPI from "../api/authAPI";

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
  const navigate = useNavigate();

  const handleRegister = async (values) => {
    try {
      const payload = {
        fullName: values.fullName,
        email: values.email,
        phone: values.phone,
        password: values.password,
        driverLicense: values.driverLicense,
        vehicleNumber: values.vehicleNumber,
        vehicleType: values.vehicleType,
        dateOfBirth: values.dateOfBirth
          ? values.dateOfBirth.format("YYYY-MM-DD")
          : null,
        address: values.address,
      };

      const res = await authAPI.postResgisterUser(payload);
      console.log("Register success:", res);
      message.success("Register successfully!");
      navigate("/login");
    } catch (error) {
      console.error("Register failed:", error);
      message.error("Register failed!");
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-[#798583] px-4 text-white">
      {/* background glow */}
      <div className="absolute inset-0 -z-10">
        <div className="absolute left-[-120px] top-24 h-[300px] w-[300px] rounded-full bg-gradient-to-br from-[#2cd06d]/15 to-transparent blur-3xl" />
        <div className="absolute right-[-160px] bottom-10 h-[260px] w-[260px] rounded-full bg-gradient-to-tr from-[#1f8a4d]/20 to-transparent blur-3xl" />
      </div>

      <Card className="mx-auto w-full max-w-lg bg-[#020203]/95 !border-[#1c2320] !shadow-[0_20px_60px_rgba(8,19,15,0.55)]">
        <div className="flex flex-col gap-5">
          <div className="text-center mb-2">
            <Title level={3} className="!m-0 !text-[#4ef58b]">
              EV Station
            </Title>
            <Text className="text-[#8f9a99]">Register your driver account</Text>
          </div>

          <Form
            layout="vertical"
            size="large"
            className="space-y-2"
            requiredMark={false}
            onFinish={handleRegister}
          >
            <Row gutter={12}>
              <Col span={12}>
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
                    placeholder="Full name"
                  />
                </Form.Item>
              </Col>

              <Col span={12}>
                <Form.Item
                  name="email"
                  label="Email"
                  className={formItemClassName}
                  rules={[
                    { required: true, message: "Please enter your email" },
                    { type: "email", message: "Invalid email format" },
                  ]}
                >
                  <Input
                    prefix={<MailOutlined className="text-[#bbc9c0]" />}
                    placeholder="Email"
                  />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={12}>
              <Col span={12}>
                <Form.Item
                  name="phone"
                  label="Phone"
                  className={formItemClassName}
                  rules={[
                    { required: true, message: "Please enter your phone" },
                  ]}
                >
                  <Input
                    prefix={<PhoneOutlined className="text-[#bbc9c0]" />}
                    placeholder="Phone number"
                  />
                </Form.Item>
              </Col>

              <Col span={12}>
                <Form.Item
                  name="dateOfBirth"
                  label="Birth date"
                  className={formItemClassName}
                  rules={[
                    {
                      required: true,
                      message: "Please select your birth date",
                    },
                  ]}
                >
                  <DatePicker
                    format="YYYY-MM-DD"
                    style={{ width: "100%" }}
                    className="!bg-[#0c1013] !border-[#1f2a24] !text-[#e9ffee] 
               !placeholder:text-[#5a6b63] 
               [&_.ant-picker-suffix]:!text-[#bbc9c0]"
                  />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={12}>
              <Col span={12}>
                <Form.Item
                  name="driverLicense"
                  label="Driver license"
                  className={formItemClassName}
                  rules={[
                    { required: true, message: "Please enter your license" },
                  ]}
                >
                  <Input
                    prefix={<IdcardOutlined className="text-[#bbc9c0]" />}
                    placeholder="License number"
                  />
                </Form.Item>
              </Col>

              <Col span={12}>
                <Form.Item
                  name="vehicleNumber"
                  label="Vehicle number"
                  className={formItemClassName}
                  rules={[
                    { required: true, message: "Please enter vehicle number" },
                  ]}
                >
                  <Input
                    prefix={<CarOutlined className="text-[#bbc9c0]" />}
                    placeholder="e.g., 51F-123.45"
                  />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={12}>
              <Col span={12}>
                <Form.Item
                  name="vehicleType"
                  label="Vehicle type"
                  className={formItemClassName}
                  rules={[
                    { required: true, message: "Please enter vehicle type" },
                  ]}
                >
                  <Input
                    prefix={<CarOutlined className="text-[#bbc9c0]" />}
                    placeholder="Car / Motorbike"
                  />
                </Form.Item>
              </Col>

              <Col span={12}>
                <Form.Item
                  name="address"
                  label="Address"
                  className={formItemClassName}
                  rules={[
                    { required: true, message: "Please enter your address" },
                  ]}
                >
                  <Input
                    prefix={<HomeOutlined className="text-[#bbc9c0]" />}
                    placeholder="Your address"
                  />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={12}>
              <Col span={12}>
                <Form.Item
                  name="password"
                  label="Password"
                  className={formItemClassName}
                  rules={[
                    { required: true, message: "Please create a password" },
                  ]}
                >
                  <Input.Password
                    prefix={<LockOutlined className="text-[#bbc9c0]" />}
                    placeholder="Password"
                  />
                </Form.Item>
              </Col>

              <Col span={12}>
                <Form.Item
                  name="confirm"
                  label="Confirm"
                  className={formItemClassName}
                  dependencies={["password"]}
                  rules={[
                    { required: true, message: "Please confirm password" },
                    ({ getFieldValue }) => ({
                      validator(_, value) {
                        if (!value || getFieldValue("password") === value)
                          return Promise.resolve();
                        return Promise.reject(
                          new Error("Passwords do not match")
                        );
                      },
                    }),
                  ]}
                >
                  <Input.Password
                    prefix={<LockOutlined className="text-[#bbc9c0]" />}
                    placeholder="Confirm"
                  />
                </Form.Item>
              </Col>
            </Row>

            <Button
              type="primary"
              htmlType="submit"
              className="mt-2 h-11 w-full rounded-full border-none bg-gradient-to-r from-[#2729b8] to-[#49eb85] text-base font-semibold text-[#042410] shadow-[0_10px_20px_rgba(18,70,38,0.35)] transition hover:from-[#34c759] hover:to-[#45e47d]"
            >
              Register
            </Button>

            <div className="text-center mt-2 text-sm text-[#8f9a99]">
              Already have an account?
              <RouterLink
                to="/login"
                className="ml-1 font-medium text-[#7cf69a] hover:text-[#b9ffda]"
              >
                Sign in
              </RouterLink>
            </div>
          </Form>
        </div>
      </Card>
    </div>
  );
}

export default RegisterPage;
