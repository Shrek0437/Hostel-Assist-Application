import { useNavigate } from "react-router-dom";

export default function Home() {
  const navigate = useNavigate();

  return (
    <>
      <h1 className="text-3xl font-bold my-4 ml-2">
        Hostel Notice Board System
      </h1>

      <div className="ml-3">
        <button
          onClick={() => navigate("/admin")}
          className="block mb-2 border-2 border-black px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 hover:cursor-pointer transition"
        >
          Enter as Admin
        </button>
        <button
          onClick={() => navigate("/student")}
          className="border-2 border-black px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
        >
          Enter as Student
        </button>
      </div>
    </>
  );
}
