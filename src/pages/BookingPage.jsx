import React from "react";
import { Form, Input, Button, DatePicker, Select, message } from "antd";
import dayjs from "dayjs";
import axios from "axios";

function BookingPage() {
  const [form] = Form.useForm();

  const onFinish = async (values) => {
    try {
      // Convert DatePicker value (dayjs) sang ISO string
      const payload = {
        stationId: Number(values.stationId),
        connectorType: values.connectorType,
        startTime: values.startTime.toISOString(), // ✅ ISO format
      };

      console.log("Payload gửi API:", payload);

      const res = await axios.post(
        "http://localhost:8080/api/bookings",
        payload
      );
      message.success("Đặt xe thành công!");
      console.log(res.data);
    } catch (error) {
      console.error(error);
      message.error("Lỗi khi gửi yêu cầu!");
    }
  };
  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={onFinish}
      style={{ maxWidth: 500, margin: "0 auto", marginTop: 400 }}
    >
      <Form.Item
        label="Mã trạm (stationId)"
        name="stationId"
        rules={[{ message: "Trạm sạc" }]}
      >
        <Input type="number" placeholder="Nhập mã trạm..." />
      </Form.Item>

      <Form.Item
        label="Loại cổng sạc (connectorType)"
        name="connectorType"
        rules={[{ message: "Vui lòng chọn loại cổng sạc!" }]}
      >
        <Select placeholder="Chọn loại cổng">
          <Select.Option value="CCS">CCS</Select.Option>
          <Select.Option value="CHAdeMO">CHAdeMO</Select.Option>
          <Select.Option value="AC">AC Type 2</Select.Option>
        </Select>
      </Form.Item>

      <Form
        form={form}
        layout="vertical"
        initialValues={{
          startTime: dayjs().add(15, "minute"), // mặc định sau 15 phút
        }}
      >
        <Form.Item label="Thời gian bắt đầu " name="startTime">
          <DatePicker
            showTime={{
              format: "HH:mm",
              minuteStep: 15, // ✅ chỉ cho chọn 00, 15, 30, 45
            }}
            format="YYYY-MM-DD HH:mm"
            style={{ width: "32%" }}
            disabledDate={(current) =>
              current && current < dayjs().startOf("day")
            }
          />
        </Form.Item>
      </Form>

      <Form.Item>
        <Button
          type="primary"
          htmlType="submit"
          block
          className="h-15 w-full rounded-full border-none bg-gradient-to-r  text-base font-semibold text-[#fcfcfc] shadow-[0_15px_30px_rgba(18,70,38,0.35)] "
        >
          Submit
        </Button>
      </Form.Item>
    </Form>
  );
}

export default BookingPage;
