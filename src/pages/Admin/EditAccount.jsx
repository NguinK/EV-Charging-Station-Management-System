import React, { useEffect, useState } from "react";
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  Space,
  Popconfirm,
  message,
  Row,
  Col,
  DatePicker,
  Tag,
} from "antd";
import dayjs from "dayjs";
import adminDriverAPI from "../../api/adminDriverAPI";
import { App } from "antd";

const EditAccount = () => {
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingDriver, setEditingDriver] = useState(null);
  const [form] = Form.useForm();
  const { message, modal } = App.useApp();
  // 🔹 Lấy danh sách tài xế
  const fetchDrivers = async () => {
    setLoading(true);
    try {
      const res = await adminDriverAPI.getAllDrivers();
      const onlyDrivers = res.data.filter((u) => u.role === "EV_DRIVER");
      setDrivers(onlyDrivers);
      setDrivers(res.data || []);
    } catch (error) {
      console.error(error);
      message.error("Không thể tải danh sách tài xế");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDrivers();
  }, []);

  // 🔹 Mở modal thêm
  const handleAdd = () => {
    setEditingDriver(null);
    form.resetFields();
    setIsModalOpen(true);
  };

  // 🔹 Mở modal sửa
  const handleEdit = (record) => {
    setEditingDriver(record);
    form.setFieldsValue({
      ...record,
      dateOfBirth: record.dateOfBirth ? dayjs(record.dateOfBirth) : null,
    });
    setIsModalOpen(true);
  };

  // 🔹 Xoá tài xế
  const handleDelete = async (record) => {
    // ✅ Lấy ID từ nhiều trường hợp khác nhau, ưu tiên account.id
    const accountId =
      record?.driver?.account?.id || record?.driver?.id || record?.id;

    if (!accountId) {
      message.warning("Không thể xóa vì ID chưa hợp lệ.");
      console.log("record nhận được:", record);
      return;
    }

    console.log("🧨 Xóa tài xế với ID:", accountId);

    try {
      await adminDriverAPI.deleteDriver(accountId); // DELETE /api/admin/drivers/{accountId}
      message.success("Đã xóa tài xế thành công");
      fetchDrivers();
    } catch (error) {
      console.error("Delete driver error:", error);
      message.error("Xóa thất bại, vui lòng thử lại!");
    }
  };

  // 🔹 Lưu (thêm hoặc sửa)
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        ...values,
        dateOfBirth: values.dateOfBirth
          ? values.dateOfBirth.format("YYYY-MM-DD")
          : null,
      };

      if (editingDriver) {
        await adminDriverAPI.updateDriver(editingDriver.id, payload);
        message.success("Cập nhật tài xế thành công");
      } else {
        await adminDriverAPI.createDriver(payload);
        message.success("Thêm tài xế mới thành công");
      }
      setIsModalOpen(false);
      fetchDrivers();
    } catch (error) {
      console.error(error);
      message.error("Lưu thất bại, vui lòng kiểm tra lại");
    }
  };

  // 🔹 Cấu hình cột bảng
  const columns = [
    {
      title: "ID",
      dataIndex: ["driver", "id"],
      key: "id",
      width: 80,
    },
    {
      title: "Họ tên",
      dataIndex: "fullName",
      key: "fullName",
    },
    {
      title: "Ngày sinh",
      dataIndex: "dateOfBirth",
      key: "dateOfBirth",
      render: (dob) => (dob ? new Date(dob).toLocaleDateString("vi-VN") : "-"),
    },
    {
      title: "Địa chỉ",
      dataIndex: "address",
      key: "address",
      ellipsis: true,
    },
    {
      title: "Số GPLX",
      dataIndex: "driverLicense",
      key: "driverLicense",
    },
    {
      title: "Biển số xe",
      dataIndex: "vehicleNumber",
      key: "vehicleNumber",
    },
    {
      title: "Loại xe",
      dataIndex: "vehicleType",
      key: "vehicleType",
      render: (v) => <Tag color="blue">{v}</Tag>,
    },
    {
      title: "Hành động",
      key: "actions",
      render: (_, record) => (
        <Space>
          <Button size="small" type="link" onClick={() => handleEdit(record)}>
            Sửa
          </Button>
          <Popconfirm
            title="Xác nhận xóa tài xế này?"
            okText="Xóa"
            cancelText="Hủy"
            onConfirm={() => handleDelete(record)}
          >
            <Button danger size="small" type="link">
              Xóa
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 20 }}>
      <Row justify="space-between" align="middle" style={{ marginBottom: 16 }}>
        <Col>
          <h2>Quản lý tài xế</h2>
        </Col>
        <Col>
          <Button type="primary" onClick={handleAdd}>
            + Thêm tài xế
          </Button>
        </Col>
      </Row>

      <Table
        rowKey="id"
        loading={loading}
        dataSource={drivers}
        columns={columns}
        pagination={{ pageSize: 8 }}
      />

      <Modal
        title={editingDriver ? "Sửa thông tin tài xế" : "Thêm tài xế mới"}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        onOk={handleSubmit}
        okText="Lưu"
        cancelText="Hủy"
        width={700}
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Họ tên"
                name="fullName"
                rules={[{ required: true, message: "Nhập họ tên" }]}
              >
                <Input placeholder="Nguyễn Văn A" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="Ngày sinh" name="dateOfBirth">
                <DatePicker style={{ width: "100%" }} format="YYYY-MM-DD" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Số điện thoại"
                name="phone"
                rules={[{ required: true, message: "Nhập số điện thoại" }]}
              >
                <Input placeholder="0123456789" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="Email" name="email">
                <Input placeholder="email@example.com" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item label="Địa chỉ" name="address">
            <Input placeholder="123 Đường ABC, Quận XYZ" />
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item label="Giấy phép lái xe" name="driverLicense">
                <Input placeholder="79A-XXXX" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="Biển số xe" name="vehicleNumber">
                <Input placeholder="51F-123.45" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item label="Loại xe" name="vehicleType">
                <Input placeholder="Xe máy / Ô tô" />
              </Form.Item>
            </Col>
            {!editingDriver && (
              <Col span={12}>
                <Form.Item
                  label="Mật khẩu"
                  name="password"
                  rules={[{ required: true, message: "Nhập mật khẩu" }]}
                >
                  <Input.Password placeholder="********" />
                </Form.Item>
              </Col>
            )}
          </Row>
        </Form>
      </Modal>
    </div>
  );
};

export default EditAccount;
