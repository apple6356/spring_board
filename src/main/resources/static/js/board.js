

const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('board-id').value;
        /*
        특정 정보가 필요한 클라이언트는 서버에 HTTP 통신으로 요청(request)을 보내고, 정보를 응답(response)받을 수 있다.
        이때 사용 되는 메서드가 fetch.
        */
        fetch(`/boards/${id}`, {
            method: 'DELETE'
        })
        .then(() => {
            alert('삭제 완료');
            location.replace('/boards');
        });
    });
}

const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id')

        fetch(`/board/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify ({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
        .then(() => {
            alert('수정 완료');
            location.replace(`/boards/${id}`);
        });
    })
}

const createButton = document.getElementById('create-btn');

if(createButton) {
    createButton.addEventListener('click', event => {
        fetch("/write", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById("title").value,
                content: document.getElementById("content").value,
            }),
        }).then(() => {
            alert("등록 완료");
            location.replace("/boards");
        });
    });
}
