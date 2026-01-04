import { useState, useEffect } from "react";
import { Routes, Route, useNavigate, useParams } from "react-router-dom";

function CreateNoticeForm() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: "",
    date: "",
    message: "",
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.id.replace("notice-", "")]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:5000/api/notices", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        alert("Notice Created Successfully!");
        navigate("/admin");
      }
    } catch (error) {
      console.error("Error sending data: ", error);
    }
  };

  return (
    <>
      <h1 className="text-3xl font-bold my-4 ml-2">Create Notice</h1>
      <form className="create-notice-form ml-2 mt-4" onSubmit={handleSubmit}>
        <div className="form-group mb-4">
          <label htmlFor="notice-title" className="block mb-1 font-medium">
            Notice Title:
          </label>
          <input
            type="text"
            id="notice-title"
            value={formData.title}
            onChange={handleChange}
            placeholder="Notice Title"
            required
            className="notice-title border border-gray-300 rounded px-2 py-1 w-1/4"
          />
        </div>
        <div className="form-group mb-4">
          <label htmlFor="notice-date" className="block mb-1 font-medium">
            Notice Date:
          </label>
          <input
            type="date"
            id="notice-date"
            value={formData.date}
            onChange={handleChange}
            required
            className="notice-date border border-gray-300 rounded px-2 py-1 w-1/4"
          />
        </div>
        <div className="form-group mb-4">
          <label htmlFor="notice-message" className="block mb-1 font-medium">
            Notice Message:
          </label>
          <textarea
            id="notice-message"
            value={formData.message}
            onChange={handleChange}
            required
            placeholder="Enter your message"
            className="notice-message border resize-none border-gray-300 rounded px-2 py-1 w-1/4"
            rows="4"
          ></textarea>
        </div>
        <button
          type="submit"
          className="submit-notice border-2 border-black px-4 py-2 rounded-sm bg-blue-200 hover:bg-blue-300 hover:cursor-pointer"
        >
          Create Notice
        </button>
      </form>
    </>
  );
}

function UpdateNoticeForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: "",
    date: "",
    message: "",
  });

  useEffect(() => {
    fetch("http://localhost:5000/api/notices")
      .then((res) => res.json())
      .then((allNotices) => {
        const toEdit = allNotices.find((n) => n.id === id);
        if (toEdit) setFormData(toEdit);
      });
  }, [id]);

  const handleUpdate = async (e) => {
    e.preventDefault();
    const response = await fetch(`http://localhost:5000/api/notices/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formData),
    });

    if (response.ok) {
      alert("Notice Updated!");
      navigate("/admin");
    }
  };

  return (
    <div className="ml-2">
      <h1 className="text-3xl font-bold my-4 text-yellow-600">Update Notice</h1>
      <form onSubmit={handleUpdate} className="flex flex-col gap-4 w-1/4">
        <input
          type="text"
          value={formData.title}
          className="border p-2"
          onChange={(e) => setFormData({ ...formData, title: e.target.value })}
        />
        <input
          type="date"
          value={formData.date}
          className="border p-2"
          onChange={(e) => setFormData({ ...formData, date: e.target.value })}
        />
        <textarea
          value={formData.message}
          className="border p-2"
          onChange={(e) =>
            setFormData({ ...formData, message: e.target.value })
          }
        />
        <button type="submit" className="bg-yellow-600 text-white p-2">
          Save Changes
        </button>
      </form>
    </div>
  );
}

function AdminHome() {
  const [notices, setNotices] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    fetch("http://localhost:5000/api/notices")
      .then((response) => response.json())
      .then((data) => setNotices(data))
      .catch((err) => console.error("Error fetching notices:", err));
  }, []);

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this notice?")) {
      try {
        const response = await fetch(
          `http://localhost:5000/api/notices/${id}`,
          {
            method: "DELETE",
          }
        );

        if (response.ok) {
          setNotices(notices.filter((notice) => notice.id !== id));
        } else {
          alert("Failed to delete the notice.");
        }
      } catch (error) {
        console.error("Delete error:", error);
      }
    }
  };

  return (
    <>
      <h1 className="text-3xl font-bold my-4 ml-2">
        Welcome to the Admin Side!!
      </h1>

      <div className="flex flex-col items-start ml-2 gap-2">
        <button
          onClick={() => navigate("create-notice")}
          className="border-2 border-black px-2 rounded-sm bg-gray-200 hover:cursor-pointer"
        >
          Create Notice
        </button>

        <div className="grid gap-4">
          <h2 className="text-xl font-semibold mt-4">Existing Notices:</h2>
          {notices.length === 0 ? (
            <p>No notices found.</p>
          ) : (
            <div className="flex flex-col gap-2">
              {notices.map((notice) => (
                <div
                  key={notice.id}
                  className="border p-4 rounded shadow-sm flex justify-between items-center w-1/2"
                >
                  <div>
                    <h2 className="font-bold">{notice.title}</h2>
                    <p className="text-sm text-gray-500">{notice.date}</p>
                  </div>
                  <button
                    onClick={() => navigate(`update-notice/${notice.id}`)}
                    className="bg-yellow-500 text-white px-3 py-1 rounded hover:cursor-pointer"
                  >
                    Update
                  </button>
                  <button
                    onClick={() => handleDelete(notice.id)}
                    className="bg-red-500 text-white px-3 py-1 rounded hover:cursor-pointer"
                  >
                    Delete
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </>
  );
}

export default function AdminClient() {
  return (
    <div className="Admin-container">
      <Routes>
        <Route index element={<AdminHome />} />
        <Route path="create-notice" element={<CreateNoticeForm />} />
        <Route path="update-notice/:id" element={<UpdateNoticeForm />} />
      </Routes>
    </div>
  );
}
