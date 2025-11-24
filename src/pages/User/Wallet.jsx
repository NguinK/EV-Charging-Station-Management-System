import React, { useState, useEffect } from "react";
import {
  Button,
  Card,
  Modal,
  Form,
  InputNumber,
  Input,
  message,
  notification,
  Table,
} from "antd";
import { useDispatch } from "react-redux";
import { depositToWallet } from "../../features/walletSlice";
import walletAPI from "../../api/walletAPI"; // 🆕 import API

const Wallet = () => {
  const dispatch = useDispatch();
  const [open, setOpen] = useState(false);
  const [form] = Form.useForm();

  // 🧭 Lấy user hiện tại
  const userInfo = JSON.parse(localStorage.getItem("userInfo") || "{}");
  const accountId = userInfo?.accountId || userInfo?.id;

  // 🆕 state lịch sử nạp tiền
  const [transactions, setTransactions] = useState([]);
  const [txLoading, setTxLoading] = useState(false);

  // 🆕 gọi API lịch sử nạp tiền
  useEffect(() => {
    if (!accountId) return;

    const fetchTransactions = async () => {
      try {
        setTxLoading(true);
        const res = await walletAPI.getTransactions(accountId);
        // Nếu chỉ muốn lịch nạp tiền, lọc type = "DEPOSIT"
        const deposits = (res.data || []).filter((tx) => tx.type === "DEPOSIT");
        setTransactions(deposits);
      } catch (err) {
        console.error("❌ Fetch wallet transactions error:", err);
        message.error("Không lấy được lịch nạp tiền của ví!");
      } finally {
        setTxLoading(false);
      }
    };

    fetchTransactions();
  }, [accountId]);

  const onFinish = async (values) => {
    if (!accountId) {
      message.error("Không tìm thấy accountId của người dùng!");
      return;
    }

    try {
      const res = await dispatch(
        depositToWallet({
          accountId,
          amount: Number(values.amount),
          description: values.description || "",
        })
      ).unwrap();

      notification.success({
        message: "Nạp tiền thành công!",
        description: `Nạp thành công ${values.amount} VNĐ.`,
        placement: "top",
      });

      // 🆕 Sau khi nạp thành công, load lại lịch sử
      const txRes = await walletAPI.getTransactions(accountId);
      const deposits = (txRes.data || []).filter((tx) => tx.type === "DEPOSIT");
      setTransactions(deposits);

      form.resetFields();
      setOpen(false);
    } catch (err) {
      console.error("❌ Deposit error:", err);
      message.error("Nạp tiền thất bại!");
    }
  };

  // 🆕 cấu hình cột table
  const columns = [
    {
      title: "Created At",
      dataIndex: "createdAt",
      key: "createdAt",
      render: (value) => (value ? new Date(value).toLocaleString("vi-VN") : ""),
    },
    {
      title: "Amount",
      dataIndex: "amount",
      key: "amount",
      render: (value) =>
        value?.toLocaleString("vi-VN", { minimumFractionDigits: 0 }) + " đ",
    },

    {
      title: "Desciption",
      dataIndex: "description",
      key: "description",
    },
  ];

  return (
    <div className="w-full flex justify-center mt-10">
      <Card
        title="Wallet"
        size="default"
        className="w-full shadow-lg text-center"
      >
        <Button type="primary" onClick={() => setOpen(true)} className="mb-4">
          Deposit
        </Button>

        {/* Modal nạp tiền */}
        <Modal
          title="Deposit"
          open={open}
          onCancel={() => setOpen(false)}
          onOk={() => form.submit()}
          okText="Add"
          cancelText="Cancel"
        >
          <Form form={form} layout="vertical" onFinish={onFinish}>
            <Form.Item
              label="Amount"
              name="amount"
              rules={[
                { required: true, message: "Vui lòng nhập số tiền!" },
                { type: "number", min: 1000, message: "Values must be > 0" },
              ]}
            >
              <InputNumber
                style={{ width: "100%" }}
                min={1000}
                placeholder="50,000 VNĐ"
                formatter={(value) =>
                  `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                }
                parser={(value) => value.replace(/,/g, "")}
              />
            </Form.Item>

            <Form.Item label="Description" name="description">
              <Input placeholder="Description" />
            </Form.Item>
          </Form>
        </Modal>

        {/* 🆕 Bảng lịch sử nạp tiền */}
        <h3 className="text-left font-semibold mb-2 mt-4">Lịch sử nạp tiền</h3>
        <Table
          rowKey="id"
          columns={columns}
          dataSource={transactions}
          loading={txLoading}
          pagination={{ pageSize: 5 }}
          style={{ width: "100%" }} 
          scroll={{ x: true }}
        />
      </Card>
    </div>
  );
};

export default Wallet;
