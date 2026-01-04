import { useState, useEffect } from "react";

export default function StudentClient() {
  const [notices, setNotices] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("http://localhost:5000/api/notices")
      .then((res) => res.json())
      .then((data) => {
        // Sort by date (newest first)
        const sortedData = data.sort(
          (a, b) => new Date(b.date) - new Date(a.date)
        );
        setNotices(sortedData);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error:", err);
        setLoading(false);
      });
  }, []);

  return (
    <>
      <h1 className="text-3xl font-bold my-4 ml-2">Student Notice Board</h1>

      <main className="ml-2">
        {loading ? (
          <div className="text-xl text-gray-500">Loading notices...</div>
        ) : notices.length === 0 ? (
          <div>
            <p>No notices have been posted yet.</p>
          </div>
        ) : (
          <div className="grid gap-6">
            {notices.map((notice) => (
              <div
                key={notice.id}
                className="bg-white border-2  p-2 border-black w-1/2"
              >
                <div className="flex justify-between items-start mb-3">
                  <h2 className="text-2xl font-bold uppercase">
                    {notice.title}
                  </h2>
                  <span>
                    {new Date(notice.date).toLocaleDateString("en-GB", {
                      day: "numeric",
                      month: "short",
                      year: "numeric",
                    })}
                  </span>
                </div>
                <hr className="mb-4 border-gray-100" />
                <p>{notice.message}</p>
              </div>
            ))}
          </div>
        )}
      </main>
    </>
  );
}
