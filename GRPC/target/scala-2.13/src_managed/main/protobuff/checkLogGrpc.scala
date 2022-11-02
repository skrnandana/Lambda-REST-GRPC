package protobuff

object checkLogGrpc {
  val METHOD_CHECK_TIME: _root_.io.grpc.MethodDescriptor[protobuff.LogRequest, protobuff.LogReply] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("checkLog", "checkTime"))
      .setSampledToLocalTracing(true)
      .setRequestMarshaller(_root_.scalapb.grpc.Marshaller.forMessage[protobuff.LogRequest])
      .setResponseMarshaller(_root_.scalapb.grpc.Marshaller.forMessage[protobuff.LogReply])
      .setSchemaDescriptor(_root_.scalapb.grpc.ConcreteProtoMethodDescriptorSupplier.fromMethodDescriptor(protobuff.ProtobuffProto.javaDescriptor.getServices().get(0).getMethods().get(0)))
      .build()
  
  val SERVICE: _root_.io.grpc.ServiceDescriptor =
    _root_.io.grpc.ServiceDescriptor.newBuilder("checkLog")
      .setSchemaDescriptor(new _root_.scalapb.grpc.ConcreteProtoFileDescriptorSupplier(protobuff.ProtobuffProto.javaDescriptor))
      .addMethod(METHOD_CHECK_TIME)
      .build()
  
  trait checkLog extends _root_.scalapb.grpc.AbstractService {
    override def serviceCompanion = checkLog
    def checkTime(request: protobuff.LogRequest): scala.concurrent.Future[protobuff.LogReply]
  }
  
  object checkLog extends _root_.scalapb.grpc.ServiceCompanion[checkLog] {
    implicit def serviceCompanion: _root_.scalapb.grpc.ServiceCompanion[checkLog] = this
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = protobuff.ProtobuffProto.javaDescriptor.getServices().get(0)
    def scalaDescriptor: _root_.scalapb.descriptors.ServiceDescriptor = protobuff.ProtobuffProto.scalaDescriptor.services(0)
    def bindService(serviceImpl: checkLog, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition =
      _root_.io.grpc.ServerServiceDefinition.builder(SERVICE)
      .addMethod(
        METHOD_CHECK_TIME,
        _root_.io.grpc.stub.ServerCalls.asyncUnaryCall(new _root_.io.grpc.stub.ServerCalls.UnaryMethod[protobuff.LogRequest, protobuff.LogReply] {
          override def invoke(request: protobuff.LogRequest, observer: _root_.io.grpc.stub.StreamObserver[protobuff.LogReply]): Unit =
            serviceImpl.checkTime(request).onComplete(scalapb.grpc.Grpc.completeObserver(observer))(
              executionContext)
        }))
      .build()
  }
  
  trait checkLogBlockingClient {
    def serviceCompanion = checkLog
    def checkTime(request: protobuff.LogRequest): protobuff.LogReply
  }
  
  class checkLogBlockingStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[checkLogBlockingStub](channel, options) with checkLogBlockingClient {
    override def checkTime(request: protobuff.LogRequest): protobuff.LogReply = {
      _root_.scalapb.grpc.ClientCalls.blockingUnaryCall(channel, METHOD_CHECK_TIME, options, request)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): checkLogBlockingStub = new checkLogBlockingStub(channel, options)
  }
  
  class checkLogStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[checkLogStub](channel, options) with checkLog {
    override def checkTime(request: protobuff.LogRequest): scala.concurrent.Future[protobuff.LogReply] = {
      _root_.scalapb.grpc.ClientCalls.asyncUnaryCall(channel, METHOD_CHECK_TIME, options, request)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): checkLogStub = new checkLogStub(channel, options)
  }
  
  def bindService(serviceImpl: checkLog, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition = checkLog.bindService(serviceImpl, executionContext)
  
  def blockingStub(channel: _root_.io.grpc.Channel): checkLogBlockingStub = new checkLogBlockingStub(channel)
  
  def stub(channel: _root_.io.grpc.Channel): checkLogStub = new checkLogStub(channel)
  
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = protobuff.ProtobuffProto.javaDescriptor.getServices().get(0)
  
}