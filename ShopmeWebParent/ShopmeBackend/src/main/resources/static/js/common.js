$(document).ready(function() {
	$("#cancelButton").on("click", function() {
		window.location = moduleUrl;
	});

	$("#file-image").change(function() {
		const fileSize = this.files[0].size;
		if (fileSize > 1048576) {
			this.setCustomValidity("You must choose an image less than 1MB");
			this.reportValidity();
		} else {
			this.setCustomValidity("");
			showImageThumbnail(this);
		}
	});

});

function showImageThumbnail(input) {

	const file = input.files[0];
	const reader = new FileReader();

	reader.onload = function(evt) {
		$("#thumbnail").attr("src", evt.target.result);
	};
	reader.readAsDataURL(file);
}


function showModal(title, body) {
	$("#modal-title").text(title);
	$("#modal-body").text(body);
	$("#modal-dialog").modal("show");
}

function showErrorModal(message) {
	showModal("Error", message);
}

function showWWarningModal(message) {
	showModal("Warning", message);
}