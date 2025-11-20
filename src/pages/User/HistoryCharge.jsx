import React, { useEffect, useState } from "react";
import { Card, Table, Statistic, Row, Col } from "antd";
import dayjs from "dayjs";
import transactionAPI from "../../api/transactionAPI"; // giả sử bạn có file này

const HistoryCharge = () => {
  const [payments, setPayments] = useState([]);
  const [totalAmount, setTotalAmount] = useState(0);
  const [totalCount, setTotalCount] = useState(0);

  useEffect(() => {
    const fetchPayments = async () => {
      try {
        const res = await transactionAPI.getTransactionFromSession();
        if (res.data) {
          setPayments(res.data);
          setTotalAmount(
            res.data.reduce((sum, item) => sum + (item.amount || 0), 0)
          );
          setTotalCount(res.data.length);
        }
      } catch (error) {
        console.error(error);
      }
    };
    fetchPayments();
  }, []);

  const columns = [
    {
      title: "Invoice ID",
      dataIndex: "invoiceNumber",
      key: "invoiceNumber",
      render: (text) => <span className="font-medium">{text}</span>,
    },
    {
      title: "Date",
      dataIndex: "transactionTime",
      key: "transactionTime",
      render: (time) => dayjs(time).format("YYYY-MM-DD"),
    },
    {
      title: "Method",
      dataIndex: "paymentType",
      key: "paymentType",
      render: () => "EWALLET", // bạn có thể map nếu cần linh động
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      render: (status) => (
        <span
          className={`${
            status === "SUCCESS"
              ? "text-green-600"
              : status === "PENDING"
              ? "text-yellow-500"
              : "text-red-500"
          } font-semibold`}
        >
          {status}
        </span>
      ),
    },
    {
      title: "Amount (VNĐ)",
      dataIndex: "amount",
      key: "amount",
      render: (val) =>
        val?.toLocaleString("vi-VN", {
          minimumFractionDigits: 0,
          maximumFractionDigits: 0,
        }),
      align: "right",
    },
  ];

  return (
    <div className="p-5">
      {/* Phần tổng */}
      <Row gutter={16} className="mb-6">
        <Col xs={24} md={12}>
          <Card className="rounded-2xl shadow-md">
            <Statistic
              title="Total Amount Charged"
              value={totalAmount}
              suffix="VNĐ"
              precision={0}
              valueStyle={{ color: "#16a34a" }}
            />
          </Card>
        </Col>
        <Col xs={24} md={12}>
          <Card className="rounded-2xl shadow-md">
            <Statistic
              title="Total Charging Sessions"
              value={totalCount}
              precision={0}
              valueStyle={{ color: "#2563eb" }}
            />
          </Card>
        </Col>
      </Row>

      {/* Phần bảng */}
      <Card className="rounded-2xl shadow-sm">
        <Table
          rowKey="id"
          columns={columns}
          dataSource={payments}
          pagination={{ pageSize: 5 }}
        />
      </Card>
    </div>
  );
};

export default HistoryCharge;
