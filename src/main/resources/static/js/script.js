console.log("this is script file");


const toggleSidebar = () => {

   if ($(".sidebar").is(":visible")) {
      //true
      //band karna hai
      $(".sidebar").css("display", "none")
      $(".content").css("margin-left", "0%");
   }
   else {
      //false
      //show karna hai
      $(".sidebar").css("display", "block");
      $(".content").css("margin-left", "20%");

   }
};
const search = () => {
   // console.log("searching...");
   let query = $("#search-input").val();
   console.log(query);
   if (query == "") {
      $(".search-result").hide();
   } else {
      console.log(query);
      // sending request to back end server
      // ->  (`) this is a back tick used below
      let url = `http://localhost:8282/search/${query}`;
      fetch(url).then((response) => {
         return response.json();
      })
         .then((data) => {
            //data........
            // console.log(data);
            //will create loop accodrgin to us
            let text = `<div class='list-group'>`;
            // this is array so will taverse with for each
            data.forEach((contact) => {
               text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-action'> ${contact.name} </a>`
            });
            text += `</div>`;
            $(".search-result").html(text);

            $(".search-result").show();

         });

   }
};
// first request to serve create order
// we are not taking payment 
const paymentStart = () => {
   console.log("payment started...");
   // how much value we need in sts
   let amount = $("#payment_field").val();
   console.log(amount);
   //to check if amount is not blank
   if (amount == '' || amount == null) {
      alert("You have not selected any amount to Pay !!");
      return;
   }

   // code 
   // we use ajax to send request to server to create order of jquery
   // searching it from google
   $.ajax(
      {
         url: "/user/create_order",
         data: JSON.stringify({ amount: amount, info: "order_request" }),
         contentType: "application/json",
         type: "Post",
         dataType: "json",
         success: function (response) {
            console.log(response);
            if (response.status == "create") {
               // open payement form
               let options = {
                  key: "rzp_test_cNZhxink7mzn6o",
                  amount: response.amount,
                  currency: "INR",
                  name: "Smart Contact Manager",

                  description: 'Donation',
                  image: "https://img.freepik.com/free-psd/logo-mockup_35913-2089.jpg?w=740&t=st=1658942545~exp=1658943145~hmac=763087c7f21af53289ed8a219ea88b76a5c7efd4a088b937fdc5946ad84a6f5d",

                  order_id: response.id,
                  handler: function (response) {
                     console.log(response.razorpay_payment_id);
                     console.log(response.razorpay_order_id);
                     console.log(response.log(razorpay_signature));
                     console.log("payment successful...!!");
                     // console.log("congrats !! Your Payement is Successful !!");
                     updatePaymentOnServer(response.razorpay_payment_id, response.razorpay_order_id, "paid");

                  },
                  "prefill": {
                     name: "",
                     email: "",
                     contact: "",
                  },

                  notes: {
                     address: "CODING IS FUN",
                  },
                  "theme": {
                     "color": "#3399cc"
                  }

               };
               let rzp = new Razorpay(options);
               // if payment is uncessfully
               rzp.on('payment.failed', function (response) {
                  console.log(response.error.code);
                  console.log(response.error.description);
                  console.log(response.error.source);
                  console.log(response.error.step);
                  console.log(response.error.reason);
                  console.log(response.error.metadata.order_id);
                  console.log(response.error.metadata.payment_id);
                  // alert("OOPS Payments is failed!!");

               });
               rzp.open();








            };
         },
         error: function (error) {
            console.log(error);
            alert(" OOPS something went wrong...");
         },
      },
   )
     
   };   


 function updatePaymentOnServer(payment_id, order_id, status) {
   $.ajax({
      url: "/user/update_order",
      data: JSON.stringify({ payment_id: payment_id, order_id: order_id, status: status, }),
      contentType: "application/json",
      type: "Post",
      dataType: "json",
      success: function (response) {
         swal("Good job!", "congrats !! Your Payement is Successful !!!", "success");
      },
      error: function (error) {
         swal("ERROR-fail!", "Your payment is successful, but we did not get on server, we will contact you as possible ", "error");

      },
   });
 }

