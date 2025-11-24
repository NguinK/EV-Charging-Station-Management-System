import React, { useEffect, useState } from "react";
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  Space,
  Popconfirm,
 
  Row,
  Col,
  DatePicker,
  Tag,
} from "antd";

import adminDriverAPI from "../../api/adminDriverAPI";
import { App } from "antd";

const EditAccount = () => {
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingDriver, setEditingDriver] = useState(null);
  const [form] = Form.useForm();
  const { message } = App.useApp();
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
  // const handleAdd = () => {
  //   setEditingDriver(null);
  //   form.resetFields();
  //   setIsModalOpen(true);
  // };

  // 🔹 Mở modal sửa
  // const handleEdit = (record) => {
  //   setEditingDriver(record);
  //   form.setFieldsValue({
  //     ...record,
  //     dateOfBirth: record.dateOfBirth ? dayjs(record.dateOfBirth) : null,
  //   });
  //   setIsModalOpen(true);
  // };

  // 🔹 Xoá tài xế
  const handleDelete = async (record) => {
    const id = record?.id || record?.driverId; // Ưu tiên record.id vì API nhận {id}

    if (!id) {
      message.warning("Không thể xóa vì ID không hợp lệ");
      console.log("⚠️ record không có ID:", record);
      return;
    }

    console.log("🧨 Gọi API xóa tài xế ID:", id);

    try {
      const res = await adminDriverAPI.deleteDriver(id);
      console.log("✅ Phản hồi từ server:", res);
      message.success("Đã xóa tài xế thành công!");
      fetchDrivers();
    } catch (error) {
      console.error("❌ Lỗi khi xóa tài xế:", error);
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
    // {
    //   title: "ID",
    //   dataIndex: "driverId",
    //   key: "driverId",
    //   width: 80,
    // },
    {
      title: "Full Name",
      dataIndex: "fullName",
      key: "fullName",
    },
    {
      title: "Date of Birth",
      dataIndex: "dateOfBirth",
      key: "dateOfBirth",
      render: (dob) => (dob ? new Date(dob).toLocaleDateString("vi-VN") : "-"),
    },
    {
      title: "Address",
      dataIndex: "address",
      key: "address",
      ellipsis: true,
    },
    {
      title: "Driver License",
      dataIndex: "driverLicense",
      key: "driverLicense",
    },
    {
      title: "Vehicle Number",
      dataIndex: "vehicleNumber",
      key: "vehicleNumber",
    },
    {
      title: "Phone",
      dataIndex: "phone",
      key: "phone",
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email",
    },
    {
      title: "Vehicle Type",
      dataIndex: "vehicleType",
      key: "vehicleType",
      render: (v) => <Tag color="blue">{v}</Tag>,
    },
    {
      title: "Actions",
      key: "actions",
      render: (_, record) => (
        <Space>
          {/* <Button size="small" type="link" onClick={() => handleEdit(record)}>
            Sửa
          </Button> */}
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
          <h2>👤 Account Management </h2>
        </Col>
        <Col>
          {/* <Button type="primary" onClick={handleAdd}>
            + Add Driver
          </Button> */}
        </Col>
      </Row>

      <Table
        rowKey={(record) => record.id || record.driverId}
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
          
          {/* <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Connectortype"
                name="Connectortype">
                <Input placeholder=" CCS" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="batteryCapacity"
                name="batteryCapacity">
                <Input placeholder=" 33.5" />
              </Form.Item>
            </Col>
          </Row>
                 */}
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item label="Loại xe" name="vehicleType">
                <Input placeholder=" Ô tô" />
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
