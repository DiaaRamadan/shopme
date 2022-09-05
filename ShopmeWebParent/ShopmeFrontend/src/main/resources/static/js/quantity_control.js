$(document).ready(function() {

	$(".linkMinus").click(function(evt) {

		evt.preventDefault();
		const productId = $(this).attr("pid");
		const quantityInput = $("#quantity" + productId);
		const newQuantity = parseInt(quantityInput.val()) - 1;

		if (newQuantity > 0) {
			quantityInput.val(newQuantity);
		} else {
			alert("Minimum quantity is 1");
		}

	});


	$(".linkPlus").click(function(evt) {

		evt.preventDefault();
		const productId = $(this).attr("pid");
		const quantityInput = $("#quantity" + productId);
		const newQuantity = parseInt(quantityInput.val()) + 1;
		if (newQuantity <= 5) {
			quantityInput.val(newQuantity);
		} else {
			alert("Maximun quantity is 5");
		}

	});

});