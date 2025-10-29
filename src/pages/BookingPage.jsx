import React from "react";
import { Form, Input, Button, DatePicker, Select, message } from "antd";
import dayjs from "dayjs";
import reservationAPI from "../api/reservationAPI";

function BookingPage() {
  const [form] = Form.useForm();

  const onFinish = async (values) => {
    try {
      // ✅ Chuẩn bị dữ liệu gửi đi
      const payload = {
        stationId: Number(values.stationId),
        connectorType: values.connectorType,
        startTime: values.startTime.toISOString(), // ISO format
      };

      console.log("Payload gửi API:", payload);

      // ✅ Gọi API có sẵn
      const res = await reservationAPI.createReservation(payload);

      message.success("Đặt xe thành công!");
      console.log("Kết quả API:", res.data);

      // ✅ Gọi callback đóng modal nếu có
      
    } catch (error) {
      console.error("Lỗi khi gửi yêu cầu:", error);
      message.error("Lỗi khi gửi yêu cầu!");
    }
  };
  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={onFinish}
      style={{ maxWidth: 500, margin: "0 auto", marginTop: 30 }}
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
