let peers = [];
let files = []; // { peer, file }

/* ================= ADD PEER ================= */
function addPeer() {
  const peer = document.getElementById("peerInput").value.trim();
  if (!peer) return;

  peers.push(peer);
  fetchFilesFromPeer(peer);
  renderPeers();

  document.getElementById("peerInput").value = "";
}

function renderPeers() {
  const list = document.getElementById("peerList");
  list.innerHTML = "";

  peers.forEach((peer) => {
    const li = document.createElement("li");
    li.innerText = peer;
    list.appendChild(li);
  });
}

/* ================= UPLOAD ================= */
function upload() {
  const f = document.getElementById("fileInput").files[0];
  if (!f) {
    alert("Select a file");
    return;
  }

  fetch("/upload", {
    method: "POST",
    headers: { "X-Filename": f.name },
    body: f,
  }).then(() => {
    // refresh own file list after upload
    fetchFilesFromPeer("localhost");
  });
}

/* ================= FETCH FILE LIST ================= */
function fetchFilesFromPeer(peer) {
  fetch(`http://${peer}:10000/list`)
    .then((res) => res.text())
    .then((data) => {
      if (!data) return;

      data.split(",").forEach((file) => {
        // avoid duplicates
        if (!files.some((f) => f.file === file && f.peer === peer)) {
          files.push({ peer, file });
        }
      });
      renderFiles();
    });
}

/* ================= RENDER FILES ================= */
function renderFiles() {
  const list = document.getElementById("fileList");
  list.innerHTML = "";

  files.forEach((item) => {
    const li = document.createElement("li");
    li.innerHTML = `
            ${item.file} <small>(${item.peer})</small>
            <button onclick="download('${item.peer}','${item.file}')">
                Download
            </button>
        `;
    list.appendChild(li);
  });
}

/* ================= DOWNLOAD ================= */
function download(peer, file) {
  window.open(`http://${peer}:10000/download?file=${file}`);
}

/* ================= INITIAL LOAD ================= */
// load own files on page load
window.onload = () => {
  fetchFilesFromPeer("localhost");
};
