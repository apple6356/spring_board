

// summernote editor
$('#content').summernote({
    // 에디터 크기 설정
    height: 300,
    // 한글 설정
    lang: "ko-KR",
    toolbar: [
        // 글자 크기 설정
        ['fontsize', ['fontsize']],
        // 글자 [굵게, 기울임, 밑줄, 취소 선, 지우기]
        ['style', ['bold', 'italic', 'underline','strikethrough', 'clear']],
        // 서식 [글머리 기호, 번호매기기, 문단정렬]
        ['para', ['ul', 'ol', 'paragraph']],
        // 이미지 첨부
        ['insert',['picture']]
      ],
      // 추가한 글꼴
    fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New','맑은 고딕','궁서','굴림체','굴림','돋음체','바탕체'],
     // 추가한 폰트사이즈
    fontSizes: ['8','9','10','11','12','14','16','18','20','22','24','28','30','36','50','72','96'],
    callbacks : {
        onImageUpload : function(files, editor, welEditable) {
            // 다중 이미지 처리를 위해 for 문 사용
            for (var i = 0; i < files.length; i++) {
                imageUploader(files[i]);
            }
        },
        onMediaDelete: function(target) {
            deleteImage(target[0].src);
        }
    }
});

// summernote image upload
function imageUploader(file) {
    let formData = new FormData();
    formData.append('file', file);

    $.ajax({
        data: formData,
        type: "POST",
        url: "/api/image-upload",
        contentType : false,
        processData : false,
        success: function(response) {
            $('#content').summernote('insertImage', response.url);
        },
        error: function(response) {
            alert("이미지 업로드 실패: " + response.responseText);
        }
    });
}

// summernote image delete
function deleteImage(src) {
    let encodedFilename  = src.substring(src.lastIndexOf("/") + 1);
    let filename = decodeURIComponent(encodedFilename);
    console.log("Encoded Filename: ", encodedFilename);
    console.log("Decoded Filename: ", filename);

    $.ajax({
        type: "DELETE",
        url: "/api/image-delete",
        data: { filename: filename },
        success: function(response) {
//            alert(response);
            console.log(response);
        },
        error: function(response) {
            alert("삭제 실패", + response.responseText);
        }
    });
}

// beforeunload 이벤트는 window, 문서(document) 및 해당 리소스가 언로드(unload) 되려고 할 때 시작
$(window).on('beforeunload', function() {
    deleteTempImages();
});

// temp에 있는 임시 저장된 이미지 삭제
function deleteTempImages() {
    // HTML 코드에서 img 태그의 src 속성 값을 추출한다. src=로 시작하고, 뒤에 큰따옴표가 아닌 문자가 1개 이상 오는 패턴을 찾는다
    let images = $('#content').summernote('code').match(/src="([^"]+)"/g);

    if (images) {
        images = images.map(src => src.substring(5, src.length - 1)); // 'src="' 와 '"' 제거
        images.forEach(src => {
            if (src.includes('/files/temp/')) {
                deleteImage(src);
            }
        });
    }
}


