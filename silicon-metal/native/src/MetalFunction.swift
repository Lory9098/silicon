import Foundation
import Metal

@_cdecl("metal_create_function")
public func metal_create_function(
    libraryPtr: UnsafeMutableRawPointer,
    functionPtr: UnsafePointer<CChar>?
) -> UnsafeMutableRawPointer? {
    let library: MTLLibrary = pointerToObject(libraryPtr)
    guard let functionPtr = functionPtr else { return nil }

    let functionName = String(cString: functionPtr)
    guard let function = library.makeFunction(name: functionName) else { return nil }

    return objectToPointer(function)
}

@_cdecl("metal_max_work_group_size")
public func metal_max_work_group_size(
    pipelinePtr: UnsafeMutableRawPointer
) -> Int32 {
    let pipeline: MTLComputePipelineState = pointerToObject(pipelinePtr)
    return Int32(pipeline.maxTotalThreadsPerThreadgroup)
}
