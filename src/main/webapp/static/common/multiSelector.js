/*
*  用于附件上传的js方法
*/
function MultiSelector( list_target, max,elementName,staticRoot ){
	var _multiSelector = this;
    // Where to write the list
    this.list_target = list_target;
   
    this.staticRoot = staticRoot;
   
    // How many elements?
    this.count = 0;
    // How many elements?
    this.id = 0;
    // Is there a maximum?
    if( max ){
        this.max = max;
    } else {
        this.max = -1;
    };
    
    this.elementName = elementName;
    
    /**//**
     * Add a new file input element
     */
    this.addElement = function( element){
    	/**
    	try{
    		var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
    		var path = $("input[name='"+elementName+"']")[0].value;
    		alert(path);
        	if (isIE && path) {
        		var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
        		var file = fileSystem.GetFile ("d://ohealth.war");
        		fileSize = file.Size;
        		var validateFileSize = 10 * 1024 * 1024;//定义大小
        		if(fileSize > validateFileSize){
        			var isContinue = confirm("文件超出了"+(validateFileSize/1024)+"KB,是否继续添加？","aa");
        			if(!isContinue){
        				return;
        			}
        		}
        	}
    	}catch(e){
    		alert(e);
    	}
    	**/
    	
        // Make sure it's a file input element
        if( element.tagName == 'INPUT' && element.type == 'file' ){
            // Element name -- what number am I?
           // element.name = 'file_' + this.id++;
        	
            element.name = this.elementName;
                        
            // Add reference to this object
            element.multi_selector = this;

            // What to do when a file is selected
            element.onchange = function(){ 
                // New file input
                var new_element = document.createElement( 'input' );
                new_element.type = 'file';
                new_element.size = 1;
                new_element.className = "addfile";
                

                // Add new element
                this.parentNode.insertBefore( new_element, this );

                // Apply 'update' to element
                this.multi_selector.addElement( new_element );

                // Update list
                this.multi_selector.addListRow( this );

                // Hide this: we can't use display:none because Safari doesn't like it
                this.style.position = 'absolute';
                this.style.left = '-1000px';
            };
            
            // If we've reached maximum number, disable input element
            if( this.max != -1 && this.count >= this.max ){
                element.disabled = true;
            };

            // File element counter
            this.count++;
            // Most recent element
            this.current_element = element;
            
        } else {
            // This can only be applied to file input elements!
            alert( 'Error: not a file input element' );
        };

    };
    

    /**//**
     * Add a new row to the list of files
     */
    this.addListRow = function( element ){

        // Row div
        var new_row = document.createElement( 'div' );

        
        // Delete button
        /*var new_row_button = document.createElement( 'input' );
        new_row_button.type = 'button';
        new_row_button.value = 'Delete';*/
        
        var new_row_button = document.createElement( 'img' );
        new_row_button.src = this.staticRoot+'/images/cross.gif';
        new_row_button.style.cursor='hand';
        

        // References
        new_row.element = element;

        // Delete function
        new_row_button.onclick= function(){
           
            // Remove element from form
            this.parentNode.element.parentNode.removeChild( this.parentNode.element );

            // Remove this row from the list
            this.parentNode.parentNode.removeChild( this.parentNode );

            // Decrement counter
            this.parentNode.element.multi_selector.count--;

            // Re-enable input element (if it's disabled)
            this.parentNode.element.multi_selector.current_element.disabled = false;

            // Appease Safari
            //    without it Safari wants to reload the browser window
            //    which nixes your already queued uploads
            return false;
        };

        // Set row value
        //new_row.innerHTML = element.value;     
        
	    var values=element.value.split('\\');
	    new_row.innerHTML = values[values.length-1];

        // Add button
        new_row.appendChild( new_row_button );

        // Add it to the list
        this.list_target.appendChild( new_row );
        
    };
    
    this.outAddListRow = function(element,attachHTML,attachId,flag,attachsIdArr,index){
    	// set currentElement to element(if not, when add file, the element is null)
    	if (element == null) {
    		element = _multiSelector.current_element;
    	}
    	
        // Row div
        var new_row = document.createElement( 'div' );

        
        // Delete button    
        var new_row_button = document.createElement( 'img' );
        new_row_button.src = this.staticRoot+'/images/cross.gif';
        new_row_button.style.cursor='hand';

        // Delete function
        new_row_button.onclick= function(){
			if(flag){
				new_row_button.parentNode.parentNode.removeChild( new_row_button.parentNode );
				attachsIdArr.splice(index,1);
			}
			else{
            // Remove this row from the list
	            Ext.MessageBox.confirm('提示','您将删除此附件,是否确认？',
		           function(btn,text){
		               if (btn == 'yes'){
					       new_row_button.parentNode.parentNode.removeChild( new_row_button.parentNode );
	                       element.disabled = false;
	                       if (attachId==undefined)
	                            delDoc();
	                       else{                                   
	                         	delAttach(attachId);
	                       }
					   }
		           }
				);  
			}
            return false;
        };

        // Set row value 
        
	    new_row.innerHTML = attachHTML;

        // Add button
        if (element!=null)
          new_row.appendChild( new_row_button );

        // Add it to the list
        this.list_target.appendChild( new_row );
    }
}; 
