import React, { useState } from "react";
import {
  Button,
  Card,
  Modal,
  Form,
  InputNumber,
  Input,
  message,
  notification,
} from "antd";
import { useDispatch } from "react-redux";
import { depositToWallet } from "../../features/walletSlice";

const Wallet = () => {
  const dispatch = useDispatch();
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();

  // 🧭 Lấy user hiện tại
  const userInfo = JSON.parse(localStorage.getItem("userInfo") || "{}");
  const driverID = userInfo?.driverId || userInfo?.id;

  const onFinish = async (values) => {
    if (!driverID) {
      message.error("Không tìm thấy tài khoản người dùng!");
      return;
    }

    try {
      const res = await dispatch(
        depositToWallet({
          accountId: driverID, // 👈 truyền id tự động
          amount: Number(values.amount),
          description: values.description || "",
        })
      ).unwrap(); 
      notification.success({
        message: "Nạp tiền thành công!",
        description: `Nạp thành công ${values.amount} VNĐ.`,
        placement: "top",
      });
      form.resetFields();
      setOpen(false);
    } catch (err) {
      console.error("❌ Deposit error:", err);
      message.error("Nạp tiền thất bại!");
    }
  };

  return (
    <div className="w-full flex justify-center mt-10">
      <Card
        title="Wallet"
        size="default"
        className="w-[480px] shadow-lg text-center"
      >
        <Button type="primary" onClick={() => setOpen(true)}>
          Deposit
        </Button>

        <Modal
          title="Deposit"
          open={open}
          onCancel={() => setOpen(false)}
          onOk={() => form.submit()}
          okText="Xác nhận"
          cancelText="Hủy"
        >
          <Form form={form} layout="vertical" onFinish={onFinish}>
            <Form.Item
              label="Amount"
              name="amount"
              rules={[{ required: true, message: "Vui lòng nhập số tiền!" }]}
            >
              <InputNumber
                style={{ width: "100%" }}
                min={1000}
                placeholder="VD: 50,000"
                formatter={(value) =>
                  `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                }
                parser={(value) => value.replace(/,/g, "")}
              />
            </Form.Item>

            <Form.Item label="Description" name="description">
              <Input placeholder="Nhập mô tả (nếu có)" />
            </Form.Item>
          </Form>
        </Modal>
      </Card>
    </div>
  );
};

export default Wallet;
